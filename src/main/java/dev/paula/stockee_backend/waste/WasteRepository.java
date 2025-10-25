package dev.paula.stockee_backend.waste;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WasteRepository extends JpaRepository<WasteEntity, Long> {
    List<WasteEntity> findByIngredientId(Long ingredientId);
    List<WasteEntity> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
}