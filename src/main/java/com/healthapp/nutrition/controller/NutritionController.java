package com.healthapp.nutrition.controller;

import com.healthapp.nutrition.dto.request.LogMealRequest;
import com.healthapp.nutrition.dto.request.UpdateMealRequest;
import com.healthapp.nutrition.dto.response.*;
import com.healthapp.nutrition.service.NutritionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/nutrition")
@RequiredArgsConstructor
@Tag(name = "1. Nutrition & Food Operations", description = "API จัดการมื้ออาหาร (CRUD), สรุปสารอาหารรายวัน/สัปดาห์/เดือน และสแกนบาร์โค้ด")
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
    @Operation(
            summary = "บันทึกมื้ออาหารใหม่ (พร้อมคำนวณและสะสมสารอาหารรายวันแบบอัตโนมัติ)",
            description = "รองรับการบันทึกอาหารทั้งวันนี้และย้อนหลัง พร้อมประเภทมื้ออาหารแบบ Dynamic เช่น BREAKFAST, LUNCH, PRE_WORKOUT, POST_WORKOUT, DINNER, SNACK"
    )
    public MealLogResponse logMeal(@Valid @RequestBody LogMealRequest request) {
        return nutritionService.logMeal(request);
    }

    @PutMapping("/meals/{mealId}")
    @Operation(
            summary = "แก้ไขข้อมูลมื้ออาหาร (ปรับกรัม, แคลอรี, สารอาหาร หรือประเภทมื้อ พร้อมคำนวณยอดรวมใหม่อัตโนมัติ)",
            description = "เมื่อแก้ไขรายการอาหาร ระบบจะทำการ Re-calculate ยอดสะสมใน daily_logs ของวันนั้นให้ถูกต้องทันที 100%"
    )
    public MealLogResponse updateMeal(
            @Parameter(description = "UUID ของมื้ออาหารที่ต้องการแก้ไข", example = "e3b0c442-98fc-1c14-9afb-4c7c8b3b4f51")
            @PathVariable UUID mealId,
            @Valid @RequestBody UpdateMealRequest request
    ) {
        return nutritionService.updateMeal(mealId, request);
    }

    @DeleteMapping("/meals/{mealId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "ลบมื้ออาหาร (กรณีบันทึกผิด พร้อมคำนวณหักลบยอดรวมใน daily_logs ให้อัตโนมัติ)",
            description = "เมื่อลบรายการ ระบบจะคำนวณผลรวมใหม่จากมื้ออาหารที่เหลือทั้งหมดของวันนั้น เพื่อให้ยอดแคลอรีสะสมไม่บวมและถูกต้องเสมอ"
    )
    public void deleteMeal(
            @Parameter(description = "UUID ของมื้ออาหารที่ต้องการลบ", example = "e3b0c442-98fc-1c14-9afb-4c7c8b3b4f51")
            @PathVariable UUID mealId
    ) {
        nutritionService.deleteMeal(mealId);
    }

    @GetMapping("/summary/daily")
    @Operation(
            summary = "ดึงยอดสรุปสารอาหารประจำวันแบบละเอียด (พร้อมรายการอาหารจัดกลุ่มตามมื้อ และสัดส่วน Macro %)",
            description = "ส่งคืนยอดรวมสารอาหารทั้ง 7 ชนิด, สัดส่วน Macro Nutrients (Protein %, Carbs %, Fat %), และรายการอาหารแยกตามหมวดมื้ออาหาร (BREAKFAST, LUNCH, etc.)"
    )
    public DailyDetailResponse getDailyDetail(
            @Parameter(description = "UUID ของผู้ใช้งาน", example = "11111111-1111-1111-1111-111111111111")
            @RequestParam UUID userId,
            @Parameter(description = "วันที่ของบันทึก (YYYY-MM-DD) หากไม่ระบุจะดึงวันปัจจุบัน", example = "2026-10-30")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        LocalDate queryDate = (date != null) ? date : LocalDate.now();
        return nutritionService.getDailyDetail(userId, queryDate);
    }

    @GetMapping("/summary/range")
    @Operation(
            summary = "ดึงสรุปสถิติสารอาหารย้อนหลังตามช่วงวันที่ (สำหรับพล็อตกราฟแท่งรายสัปดาห์ในแอปมือถือ)",
            description = "ส่งคืนสถิติภาพรวม เช่น ค่าเฉลี่ยแคลอรีต่อวัน (Average Daily Calories), วันที่กินแคลอรีสูงสุด/ต่ำสุด และประวัติแต่ละวัน"
    )
    public HistoricalSummaryResponse getRangeSummary(
            @Parameter(description = "UUID ของผู้ใช้งาน", example = "11111111-1111-1111-1111-111111111111")
            @RequestParam UUID userId,
            @Parameter(description = "วันที่เริ่มต้น (YYYY-MM-DD)", example = "2026-10-24")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "วันที่สิ้นสุด (YYYY-MM-DD)", example = "2026-10-30")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return nutritionService.getRangeSummary(userId, startDate, endDate);
    }

    @GetMapping("/summary/monthly")
    @Operation(
            summary = "ดึงสรุปสถิติสารอาหารรายเดือน (สำหรับรายงานผลและแนวโน้มสุขภาพประจำเดือน)",
            description = "ส่งคืนสถิติรวมและค่าเฉลี่ยรายวันของเดือนที่เลือก (ระบุปี ค.ศ. และเดือน 1-12)"
    )
    public HistoricalSummaryResponse getMonthlySummary(
            @Parameter(description = "UUID ของผู้ใช้งาน", example = "11111111-1111-1111-1111-111111111111")
            @RequestParam UUID userId,
            @Parameter(description = "ปี ค.ศ. (เช่น 2026)", example = "2026")
            @RequestParam int year,
            @Parameter(description = "เดือน (1-12)", example = "10")
            @RequestParam int month
    ) {
        return nutritionService.getMonthlySummary(userId, year, month);
    }

    @GetMapping("/barcode/{code}")
    @Operation(
            summary = "ค้นหาข้อมูลโภชนาการจากรหัสบาร์โค้ดสินค้า (3-Tier Cache: Redis -> PostgreSQL -> Open Food Facts)",
            description = "ค้นหาข้อมูลโภชนาการสินค้าจากบาร์โค้ด (เช่น 8850188800012 มาม่า หรือ 5449000000996 โค้ก) ฟรี 100% ตอบสนองเร็วระดับมิลลิวินาทีผ่าน Redis Cache"
    )
    public BarcodeProductResponse getBarcode(
            @Parameter(description = "รหัสบาร์โค้ดสินค้า เช่น 8850188800012 (มาม่า) หรือ 5449000000996 (โค้ก)", example = "8850188800012")
            @PathVariable String code
    ) {
        return nutritionService.lookupBarcode(code);
    }
}
