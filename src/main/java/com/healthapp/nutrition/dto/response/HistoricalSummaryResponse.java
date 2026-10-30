package com.healthapp.nutrition.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Schema(description = "ผลลัพธ์สรุปสถิติสารอาหารย้อนหลัง (รายสัปดาห์ / ช่วงวันที่ / รายเดือน) สำหรับพล็อตกราฟในแอปมือถือ")
public record HistoricalSummaryResponse(
    @Schema(description = "UUID ของผู้ใช้งาน", example = "11111111-1111-1111-1111-111111111111")
    UUID userId,

    @Schema(description = "วันที่เริ่มต้นของช่วงข้อมูล", example = "2026-10-24")
    LocalDate startDate,

    @Schema(description = "วันที่สิ้นสุดของช่วงข้อมูล", example = "2026-10-30")
    LocalDate endDate,

    @Schema(description = "จำนวนวันที่บันทึกข้อมูลสำเร็จ", example = "7")
    int totalDaysLogged,

    @Schema(description = "ค่าเฉลี่ยพลังงานต่อวัน (กิโลแคลอรี)", example = "1850.5")
    double averageCaloriesPerDay,

    @Schema(description = "ค่าเฉลี่ยโปรตีนต่อวัน (กรัม)", example = "85.2")
    double averageProteinGPerDay,

    @Schema(description = "ค่าเฉลี่ยคาร์โบไฮเดรตต่อวัน (กรัม)", example = "210.0")
    double averageCarbsGPerDay,

    @Schema(description = "ค่าเฉลี่ยไขมันต่อวัน (กรัม)", example = "62.4")
    double averageFatGPerDay,

    @Schema(description = "วันที่บริโภคพลังงานสูงสุด", example = "2026-10-28")
    LocalDate highestCaloriesDay,

    @Schema(description = "ปริมาณพลังงานสูงสุดในวันนั้น (กิโลแคลอรี)", example = "2350")
    int highestCalories,

    @Schema(description = "วันที่บริโภคพลังงานต่ำสุด", example = "2026-10-25")
    LocalDate lowestCaloriesDay,

    @Schema(description = "ปริมาณพลังงานต่ำสุดในวันนั้น (กิโลแคลอรี)", example = "1520")
    int lowestCalories,

    @Schema(description = "รายการสรุปยอดสารอาหารของแต่ละวันในช่วงวันที่เลือก")
    List<DailySummaryResponse> dailyRecords
) {}

