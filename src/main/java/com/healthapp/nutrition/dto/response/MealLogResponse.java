package com.healthapp.nutrition.dto.response;

import java.time.Instant;
import java.util.UUID;

public record MealLogResponse(
    UUID id,
    UUID userId,
    String mealType,
    String foodName,
    double portionWeightGrams,
    String cookingMethod,
    int calories,
    double proteinG,
    double carbsG,
    double fatG,
    double sodiumMg,
    String imageUrl,
    Instant loggedAt
) {}

