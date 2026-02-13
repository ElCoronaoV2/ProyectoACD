package com.restaurant.tec.controller;

import com.restaurant.tec.dto.MenuResenaRequest;
import com.restaurant.tec.entity.MenuResenaEntity;
import com.restaurant.tec.entity.UserEntity;
import com.restaurant.tec.repository.UserRepository;
import com.restaurant.tec.service.MenuResenaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/menus")
public class PublicMenuController {

    @Autowired
    private MenuResenaService menuResenaService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/{id}/resenas")
    public ResponseEntity<MenuResenaEntity> addResena(
            @PathVariable Long id,
            @Valid @RequestBody MenuResenaRequest request,
            Authentication authentication) {

        String email = authentication.getName();
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        return ResponseEntity.ok(menuResenaService.addResena(
                id,
                user.getId(),
                request.getPuntuacion(),
                request.getComentario()));
    }
}
