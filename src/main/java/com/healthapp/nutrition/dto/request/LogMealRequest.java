package com.healthapp.nutrition.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.UUID;

@Schema(description = "คำขอบันทึกมื้ออาหารใหม่ (รองรับทั้งบันทึกวันนี้และบันทึกย้อนหลัง พร้อมมื้ออาหารแบบ Dynamic)")
public record LogMealRequest(
    @NotNull(message = "userId is required")
    @Schema(description = "UUID ของผู้ใช้งาน", example = "11111111-1111-1111-1111-111111111111")
    UUID userId,

    @NotBlank(message = "mealType is required")
    @Schema(
        description = "ประเภทมื้ออาหาร (เช่น BREAKFAST, BRUNCH, LUNCH, AFTERNOON_TEA, DINNER, SUPPER, PRE_WORKOUT, POST_WORKOUT, SNACK, SUPPLEMENT)",
        example = "BREAKFAST"
    )
    String mealType,

    @NotBlank(message = "foodName is required")
    @Schema(description = "ชื่อรายการอาหาร", example = "ข้าวต้มกุ้ง")
    String foodName,

    @Positive(message = "portionWeightGrams must be greater than 0")
    @Schema(description = "น้ำหนักอาหาร (กรัม)", example = "350.0")
    double portionWeightGrams,

    @Schema(description = "วิธีการปรุง (BOILED, GRILLED, STIR_FRIED, DEEP_FRIED, FRESH)", example = "BOILED")
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

    @Schema(description = "วันที่ของบันทึก (ระบุหรือไม่ระบุก็ได้ หากไม่ระบุจะเป็นวันที่ปัจจุบัน)", example = "2026-10-30")
    LocalDate logDate
) {}
