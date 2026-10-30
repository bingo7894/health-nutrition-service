package com.healthapp.nutrition.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "ผลลัพธ์ข้อมูลโภชนาการจากรหัสบาร์โค้ดสินค้า (ผ่านสถาปัตยกรรม 3-Tier Cache)")
public class BarcodeProductResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "รหัสบาร์โค้ดสินค้า (GTIN / EAN-13)", example = "8850188800012")
    private String barcode;

    @Schema(description = "ชื่อสินค้า", example = "มาม่า บะหมี่กึ่งสำเร็จรูป รสต้มยำกุ้ง")
    private String productName;

    @Schema(description = "แบรนด์ / ผู้ผลิต", example = "Mama")
    private String brand;

    @Schema(description = "ขนาดบริโภคต่อหน่วย (Serving Size)", example = "55g")
    private String servingSize;

    @Schema(description = "พลังงานรวม (กิโลแคลอรี)", example = "260")
    private Integer calories;

    @Schema(description = "โปรตีน (กรัม)", example = "5.0")
    private Double proteinG;

    @Schema(description = "คาร์โบไฮเดรต (กรัม)", example = "36.0")
    private Double carbsG;

    @Schema(description = "ไขมัน (กรัม)", example = "11.0")
    private Double fatG;

    @Schema(description = "โซเดียม (มิลลิกรัม)", example = "1280.0")
    private Double sodiumMg;

    @Schema(description = "น้ำตาล (กรัม)", example = "3.0")
    private Double sugarG;

    @Schema(description = "ใยอาหาร (กรัม)", example = "2.0")
    private Double fiberG;

    @Schema(description = "ลิงก์รูปภาพสินค้า", example = "https://images.openfoodfacts.org/images/products/...")
    private String imageUrl;

    @Schema(description = "แหล่งที่มาของข้อมูล (REDIS_CACHE, LOCAL_DATABASE, OPEN_FOOD_FACTS)", example = "REDIS_CACHE")
    private String dataSource;

    @Schema(description = "สถานะว่าดึงมาจากแคชความเร็วสูงหรือไม่", example = "true")
    private Boolean cached;
}

