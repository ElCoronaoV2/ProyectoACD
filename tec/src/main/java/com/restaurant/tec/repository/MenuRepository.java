package com.restaurant.tec.repository;

import com.restaurant.tec.entity.LocalEntity;
import com.restaurant.tec.entity.MenuEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<MenuEntity, Long> {
    
    // Buscar menús por local
    List<MenuEntity> findByLocal(LocalEntity local);
    
    // Buscar menús por local ID
    List<MenuEntity> findByLocalId(Long localId);
    
    // Buscar menús disponibles
    List<MenuEntity> findByDisponibleTrue();
    
    // Buscar menús disponibles por local
    List<MenuEntity> findByLocalIdAndDisponibleTrue(Long localId);
    
    // Buscar menús que contengan un alérgeno específico
    List<MenuEntity> findByAlergenosContaining(String alergeno);
}
