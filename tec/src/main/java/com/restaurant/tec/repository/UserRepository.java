package com.restaurant.tec.repository;

import com.restaurant.tec.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);

    java.util.List<UserEntity> findByRol(com.restaurant.tec.entity.Role rol);

    long countByRol(com.restaurant.tec.entity.Role rol);

    long countByEnabled(boolean enabled);
}