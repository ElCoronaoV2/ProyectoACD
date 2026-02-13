package com.restaurant.tec.controller;

import com.restaurant.tec.entity.UserEntity;
import com.restaurant.tec.repository.UserRepository;
import com.restaurant.tec.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<com.restaurant.tec.dto.LoginResponse> getProfile(Authentication authentication) {
        UserEntity user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        return ResponseEntity.ok(new com.restaurant.tec.dto.LoginResponse(
                "dummy-token", // No refrescamos el token aqui
                user.getId(),
                user.getEmail(),
                user.getNombre(),
                user.getRol(),
                user.getAlergenos()));
    }

    @PutMapping("/profile")
    public ResponseEntity<com.restaurant.tec.dto.LoginResponse> updateProfile(@RequestBody Map<String, Object> updates,
            Authentication authentication) {
        UserEntity user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        // Basic updates
        if (updates.containsKey("nombre"))
            user.setNombre((String) updates.get("nombre"));
        if (updates.containsKey("telefono"))
            user.setTelefono((String) updates.get("telefono"));
        if (updates.containsKey("alergenos"))
            user.setAlergenos((String) updates.get("alergenos"));

        UserEntity savedUser = userRepository.save(user);

        return ResponseEntity.ok(new com.restaurant.tec.dto.LoginResponse(
                "dummy-token",
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getNombre(),
                savedUser.getRol(),
                savedUser.getAlergenos()));
    }
}
