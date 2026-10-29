package com.healthapp.nutrition.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "daily_logs", uniqueConstraints = {
    @UniqueConstraint(name = "uq_user_log_date", columnNames = {"user_id", "log_date"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "log_date", nullable = false)
    private LocalDate logDate;

    @Column(name = "total_calories_in")
    @Builder.Default
    private int totalCaloriesIn = 0;

    @Column(name = "total_protein_g")
    @Builder.Default
    private double totalProteinG = 0.0;

    @Column(name = "total_carbs_g")
    @Builder.Default
    private double totalCarbsG = 0.0;

    @Column(name = "total_fat_g")
    @Builder.Default
    private double totalFatG = 0.0;

    @Column(name = "total_sodium_mg")
    @Builder.Default
    private double totalSodiumMg = 0.0;

    @Column(name = "total_sugar_g")
    @Builder.Default
    private double totalSugarG = 0.0;

    @Column(name = "total_fiber_g")
    @Builder.Default
    private double totalFiberG = 0.0;

    @Column(name = "total_calories_burned")
    @Builder.Default
    private int totalCaloriesBurned = 0;

    @Column(name = "net_calories")
    @Builder.Default
    private int netCalories = 0;

    @Column(name = "created_at", updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();
}

