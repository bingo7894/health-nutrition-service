package com.healthapp.nutrition.service;

import com.healthapp.nutrition.dto.request.LogMealRequest;
import com.healthapp.nutrition.dto.response.BarcodeProductResponse;
import com.healthapp.nutrition.dto.response.DailySummaryResponse;
import com.healthapp.nutrition.dto.response.MealLogResponse;
import java.time.LocalDate;
import java.util.UUID;

public interface NutritionService {

    MealLogResponse logMeal(LogMealRequest request);

    DailySummaryResponse getDailySummary(UUID userId, LocalDate date);

    BarcodeProductResponse lookupBarcode(String barcode);
}
