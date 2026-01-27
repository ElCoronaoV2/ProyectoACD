package com.restaurant.tec.repository;

import com.restaurant.tec.entity.SugerenciaEntity;
import com.restaurant.tec.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SugerenciaRepository extends JpaRepository<SugerenciaEntity, Long> {
    
    // Buscar sugerencias por usuario
    List<SugerenciaEntity> findByUsuario(UserEntity usuario);
    
    // Buscar sugerencias por usuario ID
    List<SugerenciaEntity> findByUsuarioId(Long usuarioId);
    
    // Buscar sugerencias por estado
    List<SugerenciaEntity> findByEstado(SugerenciaEntity.EstadoSugerencia estado);
    
    // Buscar sugerencias pendientes (para administradores)
    List<SugerenciaEntity> findByEstadoOrderByFechaCreacionDesc(SugerenciaEntity.EstadoSugerencia estado);
    
    // Contar sugerencias por estado
    Long countByEstado(SugerenciaEntity.EstadoSugerencia estado);
}
