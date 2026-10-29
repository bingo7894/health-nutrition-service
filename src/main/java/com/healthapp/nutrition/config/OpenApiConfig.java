package com.healthapp.nutrition.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .servers(List.of(
                new Server().url("/").description("API Gateway (Default Route)"),
                new Server().url("http://localhost:8082").description("Direct Nutrition Service (Local Dev)")
            ))
            .info(new Info()
                .title("Super Health App - Nutrition & Food Service API")
                .version("v1.0")
                .description("ระบบบันทึกอาหาร ค้นหาบาร์โค้ด และสรุปยอดสารอาหารรายวัน"));
    }
}

