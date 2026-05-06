package dev.paula.stockee_backend.dish;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import dev.paula.stockee_backend.user.UserEntity;


public interface DishRepository extends JpaRepository<DishEntity, Long> {
    List<DishEntity> findAllByUser(UserEntity user);
    Optional<DishEntity> findByIdAndUser(Long id, UserEntity user);
}
