package dev.paula.stockee_backend.orders;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import dev.paula.stockee_backend.user.UserEntity;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
        Page<OrderEntity> findAllByUserOrderByOrderDateDesc(UserEntity user, PageRequest pageRequest);

}

