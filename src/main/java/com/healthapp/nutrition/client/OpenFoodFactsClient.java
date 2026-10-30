package com.healthapp.nutrition.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthapp.nutrition.dto.response.BarcodeProductResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenFoodFactsClient {

    private final ObjectMapper objectMapper;
    private final RestClient restClient = RestClient.builder().build();

    private static final String OFF_API_URL = "https://world.openfoodfacts.org/api/v2/product/{barcode}.json";
    private static final String USER_AGENT = "SuperHealthApp - Android/iOS - Version 1.0 (contact@superhealthapp.com)";

    public Optional<BarcodeProductResponse> fetchProductByBarcode(String barcode) {
        try {
            log.info("Fetching barcode {} from Open Food Facts API v2...", barcode);

            String responseBody = restClient.get()
                    .uri(OFF_API_URL, barcode)
                    .header("User-Agent", USER_AGENT)
                    .retrieve()
                    .body(String.class);

            if (responseBody == null || responseBody.isBlank()) {
                return Optional.empty();
            }

            JsonNode root = objectMapper.readTree(responseBody);
            int status = root.path("status").asInt(0);

            if (status != 1) {
                log.info("Open Food Facts status {} for barcode {}: product not found", status, barcode);
                return Optional.empty();
            }

            JsonNode product = root.path("product");
            if (product.isMissingNode() || product.isNull()) {
                return Optional.empty();
            }

            return Optional.of(mapToBarcodeResponse(barcode, product));

        } catch (Exception e) {
            log.warn("Failed to fetch product for barcode {} from Open Food Facts: {}", barcode, e.getMessage());
            return Optional.empty();
        }
    }

    private BarcodeProductResponse mapToBarcodeResponse(String barcode, JsonNode product) {
        String name = extractProductName(product);
        String brand = product.path("brands").asText("");
        String servingSize = product.path("serving_size").asText("100g");
        String imageUrl = product.path("image_front_url").asText(product.path("image_url").asText(""));

        JsonNode nutriments = product.path("nutriments");

        // Prefer per-serving if available, otherwise per-100g
        int calories = getNutrientInt(nutriments, "energy-kcal_serving", "energy-kcal_100g", "energy-kcal");
        double protein = getNutrientDouble(nutriments, "proteins_serving", "proteins_100g", "proteins");
        double carbs = getNutrientDouble(nutriments, "carbohydrates_serving", "carbohydrates_100g", "carbohydrates");
        double fat = getNutrientDouble(nutriments, "fat_serving", "fat_100g", "fat");
        double sodium = getSodiumMg(nutriments);
        double sugar = getNutrientDouble(nutriments, "sugars_serving", "sugars_100g", "sugars");
        double fiber = getNutrientDouble(nutriments, "fiber_serving", "fiber_100g", "fiber");

        return BarcodeProductResponse.builder()
                .barcode(barcode)
                .productName(name)
                .brand(brand)
                .servingSize(servingSize)
                .calories(calories)
                .proteinG(Math.round(protein * 10.0) / 10.0)
                .carbsG(Math.round(carbs * 10.0) / 10.0)
                .fatG(Math.round(fat * 10.0) / 10.0)
                .sodiumMg(Math.round(sodium * 10.0) / 10.0)
                .sugarG(Math.round(sugar * 10.0) / 10.0)
                .fiberG(Math.round(fiber * 10.0) / 10.0)
                .imageUrl(imageUrl)
                .dataSource("OPEN_FOOD_FACTS")
                .cached(false)
                .build();
    }

    private String extractProductName(JsonNode product) {
        if (product.hasNonNull("product_name_th") && !product.path("product_name_th").asText().isBlank()) {
            return product.path("product_name_th").asText();
        }
        if (product.hasNonNull("product_name") && !product.path("product_name").asText().isBlank()) {
            return product.path("product_name").asText();
        }
        if (product.hasNonNull("product_name_en") && !product.path("product_name_en").asText().isBlank()) {
            return product.path("product_name_en").asText();
        }
        return "Unknown Product";
    }

    private int getNutrientInt(JsonNode nutriments, String... keys) {
        for (String key : keys) {
            if (nutriments.hasNonNull(key)) {
                return nutriments.path(key).asInt();
            }
        }
        return 0;
    }

    private double getNutrientDouble(JsonNode nutriments, String... keys) {
        for (String key : keys) {
            if (nutriments.hasNonNull(key)) {
                return nutriments.path(key).asDouble();
            }
        }
        return 0.0;
    }

    private double getSodiumMg(JsonNode nutriments) {
        if (nutriments.hasNonNull("sodium_serving")) {
            return nutriments.path("sodium_serving").asDouble() * 1000.0;
        }
        if (nutriments.hasNonNull("sodium_100g")) {
            return nutriments.path("sodium_100g").asDouble() * 1000.0;
        }
        if (nutriments.hasNonNull("sodium")) {
            return nutriments.path("sodium").asDouble() * 1000.0;
        }
        // Fallback: salt in grams * 400 = sodium in mg
        if (nutriments.hasNonNull("salt_100g")) {
            return nutriments.path("salt_100g").asDouble() * 400.0;
        }
        return 0.0;
    }
}

