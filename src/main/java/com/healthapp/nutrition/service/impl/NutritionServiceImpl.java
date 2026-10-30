package com.healthapp.nutrition.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthapp.nutrition.client.OpenFoodFactsClient;
import com.healthapp.nutrition.dto.request.LogMealRequest;
import com.healthapp.nutrition.dto.response.BarcodeProductResponse;
import com.healthapp.nutrition.dto.response.DailySummaryResponse;
import com.healthapp.nutrition.dto.response.MealLogResponse;
import com.healthapp.nutrition.entity.DailyLogEntity;
import com.healthapp.nutrition.entity.FoodItemEntity;
import com.healthapp.nutrition.mapper.NutritionMapper;
import com.healthapp.nutrition.repository.DailyLogRepository;
import com.healthapp.nutrition.repository.FoodItemRepository;
import com.healthapp.nutrition.repository.MealItemRepository;
import com.healthapp.nutrition.service.NutritionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NutritionServiceImpl implements NutritionService {

    private final FoodItemRepository foodItemRepository;
    private final DailyLogRepository dailyLogRepository;
    private final MealItemRepository mealItemRepository;
    private final NutritionMapper nutritionMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final OpenFoodFactsClient openFoodFactsClient;
    private final ObjectMapper objectMapper;

    private static final String BARCODE_CACHE_PREFIX = "barcode:";
    private static final long CACHE_TTL_DAYS = 7;

    @Override
    public MealLogResponse logMeal(LogMealRequest request) {
        LocalDate today = LocalDate.now();

        // 1. Get or create daily log
        DailyLogEntity dailyLog = dailyLogRepository.findByUserIdAndLogDate(request.userId(), today)
                .orElseGet(() -> dailyLogRepository.save(nutritionMapper.createNewDailyLog(request.userId(), today)));

        // 2. Accumulate nutrients into daily log
        dailyLog.setTotalCaloriesIn(dailyLog.getTotalCaloriesIn() + request.calories());
        dailyLog.setTotalProteinG(dailyLog.getTotalProteinG() + request.proteinG());
        dailyLog.setTotalCarbsG(dailyLog.getTotalCarbsG() + request.carbsG());
        dailyLog.setTotalFatG(dailyLog.getTotalFatG() + request.fatG());
        dailyLog.setTotalSodiumMg(dailyLog.getTotalSodiumMg() + request.sodiumMg());
        dailyLog.setNetCalories(dailyLog.getTotalCaloriesIn() - dailyLog.getTotalCaloriesBurned());
        dailyLogRepository.save(dailyLog);

        // 3. Save meal item
        var mealEntity = nutritionMapper.toMealEntity(request, dailyLog);
        var savedMeal = mealItemRepository.save(mealEntity);

        return nutritionMapper.toMealResponse(savedMeal, request.userId());
    }

    @Override
    @Transactional(readOnly = true)
    public DailySummaryResponse getDailySummary(UUID userId, LocalDate date) {
        DailyLogEntity dailyLog = dailyLogRepository.findByUserIdAndLogDate(userId, date)
                .orElseGet(() -> nutritionMapper.createNewDailyLog(userId, date));
        return nutritionMapper.toDailySummaryResponse(dailyLog);
    }

    @Override
    public BarcodeProductResponse lookupBarcode(String barcode) {
        String cacheKey = BARCODE_CACHE_PREFIX + barcode;

        // Tier 1: Check Redis Cache (~2ms)
        try {
            Object cached = redisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                BarcodeProductResponse response;
                if (cached instanceof BarcodeProductResponse bpr) {
                    response = bpr;
                } else {
                    response = objectMapper.convertValue(cached, BarcodeProductResponse.class);
                }
                response.setDataSource("REDIS_CACHE");
                response.setCached(true);
                log.info("Barcode {} hit in Tier 1: REDIS_CACHE", barcode);
                return response;
            }
        } catch (Exception e) {
            log.warn("Redis read failed for barcode {}: {}", barcode, e.getMessage());
        }

        // Tier 2: Check PostgreSQL food_items (~10ms)
        Optional<FoodItemEntity> localProduct = foodItemRepository.findByBarcode(barcode);
        if (localProduct.isPresent()) {
            FoodItemEntity entity = localProduct.get();
            BarcodeProductResponse response = nutritionMapper.toBarcodeResponse(entity, "LOCAL_DATABASE", false);
            log.info("Barcode {} hit in Tier 2: LOCAL_DATABASE ({})", barcode, entity.getNameTh());

            // Save to Redis Cache for future fast lookups
            cacheInRedis(cacheKey, response);
            return response;
        }

        // Tier 3: Query Open Food Facts API v2 (Free global database)
        log.info("Barcode {} not in cache or DB. Querying Tier 3: Open Food Facts...", barcode);
        Optional<BarcodeProductResponse> offProduct = openFoodFactsClient.fetchProductByBarcode(barcode);
        if (offProduct.isPresent()) {
            BarcodeProductResponse response = offProduct.get();

            // Persist to PostgreSQL food_items
            try {
                FoodItemEntity entity = nutritionMapper.toFoodItemEntity(response);
                foodItemRepository.save(entity);
                log.info("Persisted barcode {} ({}) to PostgreSQL food_items", barcode, entity.getNameTh());
            } catch (Exception e) {
                log.warn("Failed to persist barcode {} to DB: {}", barcode, e.getMessage());
            }

            // Cache in Redis for 7 days
            cacheInRedis(cacheKey, response);
            return response;
        }

        // Product not found in any tier
        log.info("Barcode {} not found in Open Food Facts or local database", barcode);
        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Product with barcode '" + barcode + "' not found. You can scan or photograph your meal using AI Vision scan."
        );
    }

    private void cacheInRedis(String key, BarcodeProductResponse response) {
        try {
            redisTemplate.opsForValue().set(key, response, CACHE_TTL_DAYS, TimeUnit.DAYS);
            log.debug("Successfully cached barcode key: {}", key);
        } catch (Exception e) {
            log.warn("Failed to write to Redis for key {}: {}", key, e.getMessage());
        }
    }
}
