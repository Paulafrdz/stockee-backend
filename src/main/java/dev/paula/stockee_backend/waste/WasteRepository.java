package dev.paula.stockee_backend.waste;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

import dev.paula.stockee_backend.user.UserEntity;

@Repository
public interface WasteRepository extends JpaRepository<WasteEntity, Long> {
    List<WasteEntity> findAllByUser(UserEntity user);
    List<WasteEntity> findByIngredientIdAndUser(Long ingredientId, UserEntity user);
}