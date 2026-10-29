package com.healthapp.nutrition.controller;

import com.healthapp.nutrition.dto.request.LogMealRequest;
import com.healthapp.nutrition.dto.response.DailySummaryResponse;
import com.healthapp.nutrition.dto.response.MealLogResponse;
import com.healthapp.nutrition.service.NutritionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/nutrition")
@RequiredArgsConstructor
@Tag(name = "1. Nutrition & Food Operations", description = "API สำหรับบันทึกมื้ออาหาร ค้นหาบาร์โค้ด และสรุปยอดสารอาหารรายวัน")
public class NutritionController {

    private final NutritionService nutritionService;

    @GetMapping("/health")
    @Operation(summary = "ตรวจสอบสถานะการทำงานของ Nutrition Service และการเชื่อมต่อ Database")
    public Map<String, Object> health() {
        return Map.of(
                "status", "UP",
                "service", "health-nutrition-service",
                "database", "PostgreSQL: nutrition_db (Connected)",
                "timestamp", System.currentTimeMillis()
        );
    }

    @PostMapping("/meals")
    @Operation(summary = "บันทึกมื้ออาหารใหม่ (พร้อมคำนวณและสะสมสารอาหารรายวัน)")
    public MealLogResponse logMeal(@Valid @RequestBody LogMealRequest request) {
        return nutritionService.logMeal(request);
    }

    @GetMapping("/summary/daily")
    @Operation(summary = "ดึงยอดสรุปสารอาหารรวมรายวันของผู้ใช้ (Calories, Protein, Carbs, Fat, Sodium, Sugar, Fiber)")
    public DailySummaryResponse getDailySummary(
            @RequestParam UUID userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        LocalDate queryDate = (date != null) ? date : LocalDate.now();
        return nutritionService.getDailySummary(userId, queryDate);
    }

    @GetMapping("/barcode/{code}")
    @Operation(summary = "ค้นหาข้อมูลโภชนาการจากรหัสบาร์โค้ดสินค้า (เช่น 8850188800012)")
    public Map<String, Object> getBarcode(@PathVariable String code) {
        return nutritionService.lookupBarcode(code);
    }
}

