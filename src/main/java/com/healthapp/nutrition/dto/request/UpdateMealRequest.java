package com.healthapp.nutrition.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

@Schema(description = "คำขอแก้ไขข้อมูลมื้ออาหาร (แก้ไขชื่อ, น้ำหนัก, แคลอรี, สารอาหาร หรือประเภทมื้อ)")
public record UpdateMealRequest(
    @NotBlank(message = "mealType is required")
    @Schema(description = "ประเภทมื้ออาหาร", example = "PRE_WORKOUT")
    String mealType,

    @NotBlank(message = "foodName is required")
    @Schema(description = "ชื่อรายการอาหาร", example = "กล้วยหอม 1 ลูก + กาแฟดำ + เนยถั่ว")
    String foodName,

    @Positive(message = "portionWeightGrams must be greater than 0")
    @Schema(description = "น้ำหนักอาหาร (กรัม)", example = "150.0")
    double portionWeightGrams,

    @Schema(description = "วิธีการปรุง (BOILED, GRILLED, STIR_FRIED, DEEP_FRIED, FRESH)", example = "FRESH")
    String cookingMethod,

    @Schema(description = "พลังงานรวม (กิโลแคลอรี)", example = "200")
    int calories,

    @Schema(description = "โปรตีน (กรัม)", example = "5.0")
    double proteinG,

    @Schema(description = "คาร์โบไฮเดรต (กรัม)", example = "32.0")
    double carbsG,

    @Schema(description = "ไขมัน (กรัม)", example = "8.0")
    double fatG,

    @Schema(description = "โซเดียม (มิลลิกรัม)", example = "50.0")
    double sodiumMg,

    @Schema(description = "น้ำตาล (กรัม)", example = "16.0")
    double sugarG,

    @Schema(description = "ใยอาหาร (กรัม)", example = "4.0")
    double fiberG,

    @Schema(description = "ลิงก์รูปภาพอาหาร", example = "https://example.com/images/meal.jpg")
    String imageUrl
) {}

