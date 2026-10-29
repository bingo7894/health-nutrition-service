package com.healthapp.nutrition.repository;

import com.healthapp.nutrition.entity.MealItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface MealItemRepository extends JpaRepository<MealItemEntity, UUID> {
    List<MealItemEntity> findByDailyLogId(UUID dailyLogId);
}

