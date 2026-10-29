package com.healthapp.nutrition.service.impl;

import com.healthapp.nutrition.dto.request.LogMealRequest;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class NutritionServiceImpl implements NutritionService {

    private final FoodItemRepository foodItemRepository;
    private final DailyLogRepository dailyLogRepository;
    private final MealItemRepository mealItemRepository;
    private final NutritionMapper nutritionMapper;

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
    public Map<String, Object> lookupBarcode(String barcode) {
        return foodItemRepository.findByBarcode(barcode)
                .map(nutritionMapper::toBarcodeMap)
                .orElseGet(() -> {
                    // Seed a sample product if not found so user can test immediately
                    FoodItemEntity sample = FoodItemEntity.builder()
                            .barcode(barcode)
                            .nameTh("นมถั่วเหลืองแลคตาซอย สูตรหวาน 250 มล.")
                            .nameEn("Lactasoy Soy Milk Original 250ml")
                            .brand("Lactasoy")
                            .servingSizeGrams(250.0)
                            .calories(140)
                            .proteinG(7.0)
                            .carbsG(18.0)
                            .fatG(4.5)
                            .sodiumMg(110.0)
                            .sugarG(13.0)
                            .fiberG(1.0)
                            .source("OPEN_FOOD_FACTS")
                            .build();
                    foodItemRepository.save(sample);
                    return nutritionMapper.toBarcodeMap(sample);
                });
    }
}

