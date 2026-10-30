package com.healthapp.nutrition.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "meal_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MealItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "daily_log_id")
    private DailyLogEntity dailyLog;

    @Column(name = "meal_type", nullable = false)
    private String mealType; // BREAKFAST, LUNCH, DINNER, SNACK

    @Column(name = "food_name", nullable = false)
    private String foodName;

    @Column(name = "portion_weight_grams", nullable = false)
    private double portionWeightGrams;

    @Column(name = "cooking_method")
    private String cookingMethod; // BOILED, GRILLED, STIR_FRIED, DEEP_FRIED

    @Column(nullable = false)
    private int calories;

    @Column(name = "protein_g")
    private double proteinG;

    @Column(name = "carbs_g")
    private double carbsG;

    @Column(name = "fat_g")
    private double fatG;

    @Column(name = "sodium_mg")
    private double sodiumMg;

    @Column(name = "sugar_g")
    @Builder.Default
    private double sugarG = 0.0;

    @Column(name = "fiber_g")
    @Builder.Default
    private double fiberG = 0.0;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "logged_at", updatable = false)
    @Builder.Default
    private Instant loggedAt = Instant.now();
}

