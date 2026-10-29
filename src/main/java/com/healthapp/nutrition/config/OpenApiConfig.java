package com.healthapp.nutrition.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(new Info()
            .title("Super Health App - Nutrition & Food Service API")
            .version("v1.0")
            .description("ระบบบันทึกอาหาร ค้นหาบาร์โค้ด และสรุปยอดสารอาหารรายวัน"));
    }
}

