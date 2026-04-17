package dev.paula.stockee_backend.lotes;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LoteRepository extends JpaRepository<LoteEntity, Long> {
    List<LoteEntity> findByStockIdOrderByExpiryDateAsc(Long stockId);
}