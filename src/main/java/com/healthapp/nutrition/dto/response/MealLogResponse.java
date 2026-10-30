package com.healthapp.nutrition.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

@Schema(description = "ผลลัพธ์ข้อมูลรายการมื้ออาหารที่บันทึกแล้ว")
public record MealLogResponse(
    @Schema(description = "UUID ประจำรายการมื้ออาหาร", example = "e3b0c442-98fc-1c14-9afb-4c7c8b3b4f51")
    UUID id,

    @Schema(description = "UUID ของผู้ใช้งาน", example = "11111111-1111-1111-1111-111111111111")
    UUID userId,

    @Schema(description = "ประเภทมื้ออาหาร", example = "BREAKFAST")
    String mealType,

    @Schema(description = "ชื่อรายการอาหาร", example = "ข้าวต้มกุ้ง")
    String foodName,

    @Schema(description = "น้ำหนักอาหาร (กรัม)", example = "350.0")
    double portionWeightGrams,

    @Schema(description = "วิธีการปรุง", example = "BOILED")
    String cookingMethod,

    @Schema(description = "พลังงานรวม (กิโลแคลอรี)", example = "320")
    int calories,

    @Schema(description = "โปรตีน (กรัม)", example = "18.0")
    double proteinG,

    @Schema(description = "คาร์โบไฮเดรต (กรัม)", example = "45.0")
    double carbsG,

    @Schema(description = "ไขมัน (กรัม)", example = "6.0")
    double fatG,

    @Schema(description = "โซเดียม (มิลลิกรัม)", example = "680.0")
    double sodiumMg,

    @Schema(description = "น้ำตาล (กรัม)", example = "2.0")
    double sugarG,

    @Schema(description = "ใยอาหาร (กรัม)", example = "1.5")
    double fiberG,

    @Schema(description = "ลิงก์รูปภาพอาหาร", example = "https://example.com/images/meal.jpg")
    String imageUrl,

    @Schema(description = "วันเวลาที่บันทึกข้อมูล", example = "2026-10-30T08:30:00Z")
    Instant loggedAt
) {}
