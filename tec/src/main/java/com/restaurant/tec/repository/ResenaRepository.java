package com.restaurant.tec.repository;

import com.restaurant.tec.entity.LocalEntity;
import com.restaurant.tec.entity.ResenaEntity;
import com.restaurant.tec.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResenaRepository extends JpaRepository<ResenaEntity, Long> {

    // Buscar reseñas por local
    List<ResenaEntity> findByLocal(LocalEntity local);

    // Buscar reseñas por local ID
    List<ResenaEntity> findByLocalId(Long localId);

    // Buscar reseñas por local ID ordenadas por fecha
    List<ResenaEntity> findByLocalIdOrderByFechaCreacionDesc(Long localId);

    // Buscar reseñas por usuario
    List<ResenaEntity> findByUsuario(UserEntity usuario);

    // Buscar reseñas por usuario ID
    List<ResenaEntity> findByUsuarioId(Long usuarioId);

    // Buscar reseñas por puntuación
    List<ResenaEntity> findByPuntuacion(Integer puntuacion);

    // Buscar reseñas por local y puntuación mínima
    List<ResenaEntity> findByLocalIdAndPuntuacionGreaterThanEqual(Long localId, Integer puntuacion);

    // Calcular puntuación promedio de un local
    @Query("SELECT AVG(r.puntuacion) FROM ResenaEntity r WHERE r.local.id = :localId")
    Double calcularPuntuacionPromedio(@Param("localId") Long localId);

    // Contar reseñas por local
    Long countByLocalId(Long localId);
}
