package com.healthapp.nutrition.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Schema(description = "ผลลัพธ์สรุปข้อมูลสารอาหารประจำวันแบบละเอียด พร้อมรายการอาหารที่กินจัดกลุ่มตามมื้อ และสัดส่วน Macro %")
public record DailyDetailResponse(
    @Schema(description = "UUID ของบันทึกประจำวัน", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    UUID id,

    @Schema(description = "UUID ของผู้ใช้งาน", example = "11111111-1111-1111-1111-111111111111")
    UUID userId,

    @Schema(description = "วันที่ของบันทึก (YYYY-MM-DD)", example = "2026-10-30")
    LocalDate logDate,

    @Schema(description = "พลังงานรวมที่บริโภคทั้งหมดในวันนั้น (กิโลแคลอรี)", example = "1420")
    int totalCaloriesIn,

    @Schema(description = "โปรตีนรวมทั้งหมด (กรัม)", example = "74.5")
    double totalProteinG,

    @Schema(description = "คาร์โบไฮเดรตรวมทั้งหมด (กรัม)", example = "125.0")
    double totalCarbsG,

    @Schema(description = "ไขมันรวมทั้งหมด (กรัม)", example = "55.2")
    double totalFatG,

    @Schema(description = "โซเดียมรวมทั้งหมด (มิลลิกรัม)", example = "2350.0")
    double totalSodiumMg,

    @Schema(description = "น้ำตาลรวมทั้งหมด (กรัม)", example = "22.0")
    double totalSugarG,

    @Schema(description = "ใยอาหารรวมทั้งหมด (กรัม)", example = "8.5")
    double totalFiberG,

    @Schema(description = "พลังงานที่เผาผลาญจากการออกกำลังกาย/ก้าวเดิน (กิโลแคลอรี)", example = "350")
    int totalCaloriesBurned,

    @Schema(description = "พลังงานสุทธิ (Calories In - Calories Burned)", example = "1070")
    int netCalories,

    @Schema(description = "สัดส่วนพลังงานจากสารอาหารหลัก (Macro Nutrients %)")
    MacroDistribution macroDistribution,

    @Schema(description = "รายการอาหารทั้งหมดในวันนั้น จัดกลุ่มตามประเภทมื้ออาหาร (BREAKFAST, LUNCH, PRE_WORKOUT, etc.)")
    Map<String, List<MealLogResponse>> mealsByMealType,

    @Schema(description = "จำนวนรายการอาหารที่บันทึกทั้งหมดในวันนั้น", example = "4")
    int totalMealsCount
) {}

