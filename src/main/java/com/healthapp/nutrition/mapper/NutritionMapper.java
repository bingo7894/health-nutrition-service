package com.healthapp.nutrition.mapper;

import com.healthapp.nutrition.dto.request.LogMealRequest;
import com.healthapp.nutrition.dto.response.DailySummaryResponse;
import com.healthapp.nutrition.dto.response.MealLogResponse;
import com.healthapp.nutrition.entity.DailyLogEntity;
import com.healthapp.nutrition.entity.FoodItemEntity;
import com.healthapp.nutrition.entity.MealItemEntity;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class NutritionMapper {

    public MealItemEntity toMealEntity(LogMealRequest request, DailyLogEntity dailyLog) {
        return MealItemEntity.builder()
                .dailyLog(dailyLog)
                .mealType(request.mealType())
                .foodName(request.foodName())
                .portionWeightGrams(request.portionWeightGrams())
                .cookingMethod(request.cookingMethod() != null ? request.cookingMethod() : "BOILED")
                .calories(request.calories())
                .proteinG(request.proteinG())
                .carbsG(request.carbsG())
                .fatG(request.fatG())
                .sodiumMg(request.sodiumMg())
                .imageUrl(request.imageUrl())
                .build();
    }

    public MealLogResponse toMealResponse(MealItemEntity entity, UUID userId) {
        return new MealLogResponse(
                entity.getId(),
                userId,
                entity.getMealType(),
                entity.getFoodName(),
                entity.getPortionWeightGrams(),
                entity.getCookingMethod(),
                entity.getCalories(),
                entity.getProteinG(),
                entity.getCarbsG(),
                entity.getFatG(),
                entity.getSodiumMg(),
                entity.getImageUrl(),
                entity.getLoggedAt()
        );
    }

    public DailyLogEntity createNewDailyLog(UUID userId, LocalDate date) {
        return DailyLogEntity.builder()
                .userId(userId)
                .logDate(date)
                .totalCaloriesIn(0)
                .totalProteinG(0.0)
                .totalCarbsG(0.0)
                .totalFatG(0.0)
                .totalSodiumMg(0.0)
                .totalSugarG(0.0)
                .totalFiberG(0.0)
                .totalCaloriesBurned(0)
                .netCalories(0)
                .build();
    }

    public DailySummaryResponse toDailySummaryResponse(DailyLogEntity entity) {
        return new DailySummaryResponse(
                entity.getId(),
                entity.getUserId(),
                entity.getLogDate(),
                entity.getTotalCaloriesIn(),
                entity.getTotalProteinG(),
                entity.getTotalCarbsG(),
                entity.getTotalFatG(),
                entity.getTotalSodiumMg(),
                entity.getTotalSugarG(),
                entity.getTotalFiberG(),
                entity.getTotalCaloriesBurned(),
                entity.getNetCalories()
        );
    }

    public Map<String, Object> toBarcodeMap(FoodItemEntity entity) {
        Map<String, Object> result = new HashMap<>();
        result.put("barcode", entity.getBarcode());
        result.put("found", true);
        result.put("name_th", entity.getNameTh());
        result.put("name_en", entity.getNameEn());
        result.put("brand", entity.getBrand());
        result.put("serving_size_grams", entity.getServingSizeGrams());
        result.put("calories", entity.getCalories());
        result.put("protein_g", entity.getProteinG());
        result.put("carbs_g", entity.getCarbsG());
        result.put("fat_g", entity.getFatG());
        result.put("sodium_mg", entity.getSodiumMg());
        result.put("sugar_g", entity.getSugarG());
        result.put("fiber_g", entity.getFiberG());
        result.put("image_url", entity.getImageUrl());
        return result;
    }
}

