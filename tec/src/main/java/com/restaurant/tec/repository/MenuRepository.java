package com.restaurant.tec.repository;

import com.restaurant.tec.entity.MenuEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<MenuEntity, Long> {
    List<MenuEntity> findByLocalId(Long localId);

    List<MenuEntity> findByPropietarioAndLocalIsNull(com.restaurant.tec.entity.UserEntity propietario);
}
