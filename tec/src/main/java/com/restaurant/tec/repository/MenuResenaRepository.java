package com.restaurant.tec.repository;

import com.restaurant.tec.entity.MenuResenaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuResenaRepository extends JpaRepository<MenuResenaEntity, Long> {

    List<MenuResenaEntity> findByMenuId(Long menuId);

    Optional<MenuResenaEntity> findByMenuIdAndUsuarioId(Long menuId, Long usuarioId);

    @Query("SELECT AVG(mr.puntuacion) FROM MenuResenaEntity mr WHERE mr.menu.id = :menuId")
    Double getAverageRatingByMenuId(Long menuId);
}
