package com.healthapp.nutrition.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthapp.nutrition.client.OpenFoodFactsClient;
import com.healthapp.nutrition.dto.request.LogMealRequest;
import com.healthapp.nutrition.dto.request.UpdateMealRequest;
import com.healthapp.nutrition.dto.response.*;
import com.healthapp.nutrition.entity.DailyLogEntity;
import com.healthapp.nutrition.entity.FoodItemEntity;
import com.healthapp.nutrition.entity.MealItemEntity;
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
import java.util.List;
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
        LocalDate targetDate = (request.logDate() != null) ? request.logDate() : LocalDate.now();

        // 1. Get or create daily log for target date
        DailyLogEntity dailyLog = dailyLogRepository.findByUserIdAndLogDate(request.userId(), targetDate)
                .orElseGet(() -> dailyLogRepository.save(nutritionMapper.createNewDailyLog(request.userId(), targetDate)));

        // 2. Save meal item
        MealItemEntity mealEntity = nutritionMapper.toMealEntity(request, dailyLog);
        MealItemEntity savedMeal = mealItemRepository.save(mealEntity);

        // 3. Atomically recalculate daily totals from all meals in that day
        recalculateDailyLog(dailyLog);

        log.info("Logged meal '{}' ({}) for user {} on date {}", savedMeal.getFoodName(), savedMeal.getMealType(), request.userId(), targetDate);
        return nutritionMapper.toMealResponse(savedMeal, request.userId());
    }

    @Override
    public MealLogResponse updateMeal(UUID mealId, UpdateMealRequest request) {
        MealItemEntity meal = mealItemRepository.findById(mealId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Meal item with id " + mealId + " not found"));

        meal.setMealType(request.mealType());
        meal.setFoodName(request.foodName());
        meal.setPortionWeightGrams(request.portionWeightGrams());
        meal.setCookingMethod(request.cookingMethod() != null ? request.cookingMethod() : "BOILED");
        meal.setCalories(request.calories());
        meal.setProteinG(request.proteinG());
        meal.setCarbsG(request.carbsG());
        meal.setFatG(request.fatG());
        meal.setSodiumMg(request.sodiumMg());
        meal.setSugarG(request.sugarG());
        meal.setFiberG(request.fiberG());
        if (request.imageUrl() != null) {
            meal.setImageUrl(request.imageUrl());
        }

        MealItemEntity updatedMeal = mealItemRepository.save(meal);

        // Recalculate daily totals
        DailyLogEntity dailyLog = updatedMeal.getDailyLog();
        recalculateDailyLog(dailyLog);

        log.info("Updated meal '{}' (id: {}). Recalculated daily log totals.", updatedMeal.getFoodName(), mealId);
        return nutritionMapper.toMealResponse(updatedMeal, dailyLog.getUserId());
    }

    @Override
    public void deleteMeal(UUID mealId) {
        MealItemEntity meal = mealItemRepository.findById(mealId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Meal item with id " + mealId + " not found"));

        DailyLogEntity dailyLog = meal.getDailyLog();
        mealItemRepository.delete(meal);
        mealItemRepository.flush();

        // Recalculate daily totals after deletion
        recalculateDailyLog(dailyLog);
        log.info("Deleted meal item {} and recalculated daily log {}", mealId, dailyLog.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public DailyDetailResponse getDailyDetail(UUID userId, LocalDate date) {
        DailyLogEntity dailyLog = dailyLogRepository.findByUserIdAndLogDate(userId, date)
                .orElseGet(() -> nutritionMapper.createNewDailyLog(userId, date));

        List<MealItemEntity> meals = (dailyLog.getId() != null)
                ? mealItemRepository.findByDailyLogIdOrderByLoggedAtAsc(dailyLog.getId())
                : List.of();

        return nutritionMapper.toDailyDetailResponse(dailyLog, meals, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public DailySummaryResponse getDailySummary(UUID userId, LocalDate date) {
        DailyLogEntity dailyLog = dailyLogRepository.findByUserIdAndLogDate(userId, date)
                .orElseGet(() -> nutritionMapper.createNewDailyLog(userId, date));
        return nutritionMapper.toDailySummaryResponse(dailyLog);
    }

    @Override
    @Transactional(readOnly = true)
    public HistoricalSummaryResponse getRangeSummary(UUID userId, LocalDate startDate, LocalDate endDate) {
        List<DailyLogEntity> logs = dailyLogRepository.findByUserIdAndLogDateBetweenOrderByLogDateAsc(userId, startDate, endDate);
        return nutritionMapper.toHistoricalSummaryResponse(userId, startDate, endDate, logs);
    }

    @Override
    @Transactional(readOnly = true)
    public HistoricalSummaryResponse getMonthlySummary(UUID userId, int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        List<DailyLogEntity> logs = dailyLogRepository.findByUserIdAndLogDateBetweenOrderByLogDateAsc(userId, startDate, endDate);
        return nutritionMapper.toHistoricalSummaryResponse(userId, startDate, endDate, logs);
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

            cacheInRedis(cacheKey, response);
            return response;
        }

        // Tier 3: Query Open Food Facts API v2 (Free global database)
        log.info("Barcode {} not in cache or DB. Querying Tier 3: Open Food Facts...", barcode);
        Optional<BarcodeProductResponse> offProduct = openFoodFactsClient.fetchProductByBarcode(barcode);
        if (offProduct.isPresent()) {
            BarcodeProductResponse response = offProduct.get();

            try {
                FoodItemEntity entity = nutritionMapper.toFoodItemEntity(response);
                foodItemRepository.save(entity);
                log.info("Persisted barcode {} ({}) to PostgreSQL food_items", barcode, entity.getNameTh());
            } catch (Exception e) {
                log.warn("Failed to persist barcode {} to DB: {}", barcode, e.getMessage());
            }

            cacheInRedis(cacheKey, response);
            return response;
        }

        log.info("Barcode {} not found in Open Food Facts or local database", barcode);
        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Product with barcode '" + barcode + "' not found. You can scan or photograph your meal using AI Vision scan."
        );
    }

    private void recalculateDailyLog(DailyLogEntity dailyLog) {
        List<MealItemEntity> remainingMeals = mealItemRepository.findByDailyLogId(dailyLog.getId());

        int totalCalories = remainingMeals.stream().mapToInt(MealItemEntity::getCalories).sum();
        double totalProtein = remainingMeals.stream().mapToDouble(MealItemEntity::getProteinG).sum();
        double totalCarbs = remainingMeals.stream().mapToDouble(MealItemEntity::getCarbsG).sum();
        double totalFat = remainingMeals.stream().mapToDouble(MealItemEntity::getFatG).sum();
        double totalSodium = remainingMeals.stream().mapToDouble(MealItemEntity::getSodiumMg).sum();
        double totalSugar = remainingMeals.stream().mapToDouble(MealItemEntity::getSugarG).sum();
        double totalFiber = remainingMeals.stream().mapToDouble(MealItemEntity::getFiberG).sum();

        dailyLog.setTotalCaloriesIn(totalCalories);
        dailyLog.setTotalProteinG(Math.round(totalProtein * 10.0) / 10.0);
        dailyLog.setTotalCarbsG(Math.round(totalCarbs * 10.0) / 10.0);
        dailyLog.setTotalFatG(Math.round(totalFat * 10.0) / 10.0);
        dailyLog.setTotalSodiumMg(Math.round(totalSodium * 10.0) / 10.0);
        dailyLog.setTotalSugarG(Math.round(totalSugar * 10.0) / 10.0);
        dailyLog.setTotalFiberG(Math.round(totalFiber * 10.0) / 10.0);
        dailyLog.setNetCalories(totalCalories - dailyLog.getTotalCaloriesBurned());

        dailyLogRepository.save(dailyLog);
        log.info("Recalculated daily log {} (date: {}): totalCalories={}", dailyLog.getId(), dailyLog.getLogDate(), totalCalories);
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
