package com.restaurant.tec.repository;

import com.restaurant.tec.entity.VerificationToken;
import com.restaurant.tec.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByToken(String token);
    Optional<VerificationToken> findByUser(UserEntity user);
    void deleteByToken(String token); // Para limpiar tokens usados
}
