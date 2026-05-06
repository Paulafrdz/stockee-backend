package dev.paula.stockee_backend.lotes;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

import dev.paula.stockee_backend.user.*;

public interface LoteRepository extends JpaRepository<LoteEntity, Long> {
    List<LoteEntity> findByStockIdAndUserOrderByExpiryDateAsc(Long stockId, UserEntity user);
}