package com.healthapp.nutrition.repository;

import com.healthapp.nutrition.entity.FoodItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FoodItemRepository extends JpaRepository<FoodItemEntity, UUID> {
    Optional<FoodItemEntity> findByBarcode(String barcode);
}

