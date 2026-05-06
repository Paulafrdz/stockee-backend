package dev.paula.stockee_backend.sales;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.paula.stockee_backend.user.UserEntity;


public interface SaleRepository extends JpaRepository<SaleEntity, Long> {
    List<SaleEntity> findAllByUser(UserEntity user);
    Optional<SaleEntity> findByIdAndUser(Long id, UserEntity user);
}

