package com.healthapp.nutrition.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "สัดส่วนพลังงานจากสารอาหารหลัก 3 ชนิด (Macro Nutrients Distribution %)")
public record MacroDistribution(
    @Schema(description = "สัดส่วนพลังงานจากโปรตีน (%)", example = "22.5")
    double proteinPercentage,

    @Schema(description = "สัดส่วนพลังงานจากคาร์โบไฮเดรต (%)", example = "37.8")
    double carbsPercentage,

    @Schema(description = "สัดส่วนพลังงานจากไขมัน (%)", example = "39.7")
    double fatPercentage
) {}

