package dev.paula.stockee_backend.stock;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.paula.stockee_backend.user.UserEntity;


public interface StockRepository extends JpaRepository<StockEntity, Long> {
    List<StockEntity> findAllByUser(UserEntity user);
    Optional<StockEntity> findByIdAndUser(Long id, UserEntity user);
}
