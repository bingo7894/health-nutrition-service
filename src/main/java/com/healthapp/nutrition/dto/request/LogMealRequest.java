package com.healthapp.nutrition.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.UUID;

public record LogMealRequest(
    @NotNull(message = "user_id is required")
    UUID userId,

    @NotBlank(message = "meal_type is required (BREAKFAST, LUNCH, DINNER, SNACK)")
    String mealType,

    @NotBlank(message = "food_name is required")
    String foodName,

    @Positive(message = "portion_weight_grams must be greater than 0")
    double portionWeightGrams,

    String cookingMethod,
    int calories,
    double proteinG,
    double carbsG,
    double fatG,
    double sodiumMg,
    String imageUrl
) {}

