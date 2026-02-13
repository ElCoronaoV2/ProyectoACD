package com.restaurant.tec.service;

import com.restaurant.tec.entity.MenuEntity;
import com.restaurant.tec.entity.MenuResenaEntity;
import com.restaurant.tec.entity.UserEntity;
import com.restaurant.tec.repository.MenuRepository;
import com.restaurant.tec.repository.MenuResenaRepository;
import com.restaurant.tec.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MenuResenaService {

    @Autowired
    private MenuResenaRepository menuResenaRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private UserRepository userRepository;

    public MenuResenaEntity addResena(Long menuId, Long userId, Integer puntuacion, String comentario) {
        MenuEntity menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new RuntimeException("Menú no encontrado"));

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Verificar si ya existe reseña
        Optional<MenuResenaEntity> existing = menuResenaRepository.findByMenuIdAndUsuarioId(menuId, userId);
        if (existing.isPresent()) {
            throw new RuntimeException("Ya has valorado este menú");
        }

        MenuResenaEntity resena = new MenuResenaEntity(user, menu, puntuacion, comentario);
        return menuResenaRepository.save(resena);
    }

    public Double getAverageRating(Long menuId) {
        Double avg = menuResenaRepository.getAverageRatingByMenuId(menuId);
        return avg != null ? avg : 0.0;
    }
}
