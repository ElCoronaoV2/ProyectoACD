package com.restaurant.tec.repository;

import com.restaurant.tec.entity.LocalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocalRepository extends JpaRepository<LocalEntity, Long> {
    java.util.List<LocalEntity> findByPropietarioId(Long propietarioId);
}