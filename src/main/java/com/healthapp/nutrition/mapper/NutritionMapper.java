package com.healthapp.nutrition.mapper;

import com.healthapp.nutrition.dto.request.LogMealRequest;
import com.healthapp.nutrition.dto.response.*;
import com.healthapp.nutrition.entity.DailyLogEntity;
import com.healthapp.nutrition.entity.FoodItemEntity;
import com.healthapp.nutrition.entity.MealItemEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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
                .sugarG(request.sugarG())
                .fiberG(request.fiberG())
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
                entity.getSugarG(),
                entity.getFiberG(),
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

    public MacroDistribution calculateMacroDistribution(double proteinG, double carbsG, double fatG) {
        double proteinCalories = proteinG * 4.0;
        double carbsCalories = carbsG * 4.0;
        double fatCalories = fatG * 9.0;
        double totalMacroCalories = proteinCalories + carbsCalories + fatCalories;

        if (totalMacroCalories <= 0.0) {
            return new MacroDistribution(0.0, 0.0, 0.0);
        }

        double proteinPct = Math.round((proteinCalories / totalMacroCalories) * 1000.0) / 10.0;
        double carbsPct = Math.round((carbsCalories / totalMacroCalories) * 1000.0) / 10.0;
        double fatPct = Math.round((fatCalories / totalMacroCalories) * 1000.0) / 10.0;

        return new MacroDistribution(proteinPct, carbsPct, fatPct);
    }

    public DailyDetailResponse toDailyDetailResponse(DailyLogEntity dailyLog, List<MealItemEntity> mealEntities, UUID userId) {
        List<MealLogResponse> mealResponses = mealEntities.stream()
                .map(m -> toMealResponse(m, userId))
                .toList();

        Map<String, List<MealLogResponse>> mealsByMealType = mealResponses.stream()
                .collect(Collectors.groupingBy(
                        MealLogResponse::mealType,
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        MacroDistribution macroDistribution = calculateMacroDistribution(
                dailyLog.getTotalProteinG(),
                dailyLog.getTotalCarbsG(),
                dailyLog.getTotalFatG()
        );

        return new DailyDetailResponse(
                dailyLog.getId(),
                dailyLog.getUserId(),
                dailyLog.getLogDate(),
                dailyLog.getTotalCaloriesIn(),
                dailyLog.getTotalProteinG(),
                dailyLog.getTotalCarbsG(),
                dailyLog.getTotalFatG(),
                dailyLog.getTotalSodiumMg(),
                dailyLog.getTotalSugarG(),
                dailyLog.getTotalFiberG(),
                dailyLog.getTotalCaloriesBurned(),
                dailyLog.getNetCalories(),
                macroDistribution,
                mealsByMealType,
                mealResponses.size()
        );
    }

    public HistoricalSummaryResponse toHistoricalSummaryResponse(UUID userId, LocalDate startDate, LocalDate endDate, List<DailyLogEntity> logs) {
        if (logs.isEmpty()) {
            return new HistoricalSummaryResponse(
                    userId,
                    startDate,
                    endDate,
                    0,
                    0.0,
                    0.0,
                    0.0,
                    0.0,
                    null,
                    0,
                    null,
                    0,
                    List.of()
            );
        }

        int totalDays = logs.size();
        double sumCalories = logs.stream().mapToInt(DailyLogEntity::getTotalCaloriesIn).sum();
        double sumProtein = logs.stream().mapToDouble(DailyLogEntity::getTotalProteinG).sum();
        double sumCarbs = logs.stream().mapToDouble(DailyLogEntity::getTotalCarbsG).sum();
        double sumFat = logs.stream().mapToDouble(DailyLogEntity::getTotalFatG).sum();

        DailyLogEntity maxCalLog = logs.stream()
                .max(Comparator.comparingInt(DailyLogEntity::getTotalCaloriesIn))
                .orElse(logs.get(0));

        DailyLogEntity minCalLog = logs.stream()
                .min(Comparator.comparingInt(DailyLogEntity::getTotalCaloriesIn))
                .orElse(logs.get(0));

        List<DailySummaryResponse> dailyResponses = logs.stream()
                .map(this::toDailySummaryResponse)
                .toList();

        return new HistoricalSummaryResponse(
                userId,
                startDate,
                endDate,
                totalDays,
                Math.round((sumCalories / totalDays) * 10.0) / 10.0,
                Math.round((sumProtein / totalDays) * 10.0) / 10.0,
                Math.round((sumCarbs / totalDays) * 10.0) / 10.0,
                Math.round((sumFat / totalDays) * 10.0) / 10.0,
                maxCalLog.getLogDate(),
                maxCalLog.getTotalCaloriesIn(),
                minCalLog.getLogDate(),
                minCalLog.getTotalCaloriesIn(),
                dailyResponses
        );
    }

    public BarcodeProductResponse toBarcodeResponse(FoodItemEntity entity, String dataSource, boolean cached) {
        String name = entity.getNameTh() != null && !entity.getNameTh().isBlank() ? entity.getNameTh() : entity.getNameEn();
        return BarcodeProductResponse.builder()
                .barcode(entity.getBarcode())
                .productName(name)
                .brand(entity.getBrand())
                .servingSize(entity.getServingSizeGrams() + "g")
                .calories(entity.getCalories())
                .proteinG(entity.getProteinG())
                .carbsG(entity.getCarbsG())
                .fatG(entity.getFatG())
                .sodiumMg(entity.getSodiumMg())
                .sugarG(entity.getSugarG())
                .fiberG(entity.getFiberG())
                .imageUrl(entity.getImageUrl())
                .dataSource(dataSource)
                .cached(cached)
                .build();
    }

    public FoodItemEntity toFoodItemEntity(BarcodeProductResponse response) {
        double servingGrams = 100.0;
        if (response.getServingSize() != null) {
            String digits = response.getServingSize().replaceAll("[^0-9.]", "");
            if (!digits.isBlank()) {
                try {
                    servingGrams = Double.parseDouble(digits);
                } catch (NumberFormatException ignored) {}
            }
        }

        return FoodItemEntity.builder()
                .barcode(response.getBarcode())
                .nameTh(response.getProductName())
                .nameEn(response.getProductName())
                .brand(response.getBrand())
                .servingSizeGrams(servingGrams)
                .calories(response.getCalories() != null ? response.getCalories() : 0)
                .proteinG(response.getProteinG() != null ? response.getProteinG() : 0.0)
                .carbsG(response.getCarbsG() != null ? response.getCarbsG() : 0.0)
                .fatG(response.getFatG() != null ? response.getFatG() : 0.0)
                .sodiumMg(response.getSodiumMg() != null ? response.getSodiumMg() : 0.0)
                .sugarG(response.getSugarG() != null ? response.getSugarG() : 0.0)
                .fiberG(response.getFiberG() != null ? response.getFiberG() : 0.0)
                .imageUrl(response.getImageUrl())
                .source("OPEN_FOOD_FACTS")
                .build();
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
