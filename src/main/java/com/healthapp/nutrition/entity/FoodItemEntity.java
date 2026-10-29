package com.healthapp.nutrition.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "food_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true)
    private String barcode;

    @Column(name = "name_th", nullable = false)
    private String nameTh;

    @Column(name = "name_en")
    private String nameEn;

    private String brand;

    @Column(name = "serving_size_grams")
    private double servingSizeGrams;

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
    private double sugarG;

    @Column(name = "fiber_g")
    private double fiberG;

    @Column(name = "image_url")
    private String imageUrl;

    private String source;

    @Column(name = "created_at", updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();
}

