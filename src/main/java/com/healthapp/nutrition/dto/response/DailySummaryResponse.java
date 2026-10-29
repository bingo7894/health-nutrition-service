package com.healthapp.nutrition.dto.response;

import java.time.LocalDate;
import java.util.UUID;

public record DailySummaryResponse(
    UUID id,
    UUID userId,
    LocalDate logDate,
    int totalCaloriesIn,
    double totalProteinG,
    double totalCarbsG,
    double totalFatG,
    double totalSodiumMg,
    double totalSugarG,
    double totalFiberG,
    int totalCaloriesBurned,
    int netCalories
) {}

