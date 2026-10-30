package com.healthapp.nutrition.service;

import com.healthapp.nutrition.dto.request.LogMealRequest;
import com.healthapp.nutrition.dto.request.UpdateMealRequest;
import com.healthapp.nutrition.dto.response.*;

import java.time.LocalDate;
import java.util.UUID;

public interface NutritionService {

    MealLogResponse logMeal(LogMealRequest request);

    MealLogResponse updateMeal(UUID mealId, UpdateMealRequest request);

    void deleteMeal(UUID mealId);

    DailyDetailResponse getDailyDetail(UUID userId, LocalDate date);

    DailySummaryResponse getDailySummary(UUID userId, LocalDate date);

    HistoricalSummaryResponse getRangeSummary(UUID userId, LocalDate startDate, LocalDate endDate);

    HistoricalSummaryResponse getMonthlySummary(UUID userId, int year, int month);

    BarcodeProductResponse lookupBarcode(String barcode);
}
