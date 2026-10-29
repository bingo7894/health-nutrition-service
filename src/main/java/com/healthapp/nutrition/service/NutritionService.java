package com.healthapp.nutrition.service;

import com.healthapp.nutrition.dto.request.LogMealRequest;
import com.healthapp.nutrition.dto.response.DailySummaryResponse;
import com.healthapp.nutrition.dto.response.MealLogResponse;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

public interface NutritionService {

    MealLogResponse logMeal(LogMealRequest request);

    DailySummaryResponse getDailySummary(UUID userId, LocalDate date);

    Map<String, Object> lookupBarcode(String barcode);
}

