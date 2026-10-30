package com.healthapp.nutrition.repository;

import com.healthapp.nutrition.entity.DailyLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DailyLogRepository extends JpaRepository<DailyLogEntity, UUID> {

    Optional<DailyLogEntity> findByUserIdAndLogDate(UUID userId, LocalDate logDate);

    List<DailyLogEntity> findByUserIdAndLogDateBetweenOrderByLogDateAsc(UUID userId, LocalDate startDate, LocalDate endDate);
}
