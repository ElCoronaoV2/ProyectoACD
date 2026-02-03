package com.restaurant.tec.controller;

import com.restaurant.tec.entity.UserEntity;
import com.restaurant.tec.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Listar todos los CEOs
    @GetMapping("/ceos")
    public ResponseEntity<List<UserEntity>> getAllCeos() {
        return ResponseEntity.ok(userRepository.findByRol(com.restaurant.tec.entity.Role.CEO));
    }

    // Listar todos los usuarios (para gestión global)
    @GetMapping("/users")
    public ResponseEntity<List<UserEntity>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    // Crear un nuevo CEO
    @PostMapping("/ceos")
    public ResponseEntity<UserEntity> createCeo(@RequestBody UserEntity ceo) {
        if (userRepository.findByEmail(ceo.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().build(); // Email ya existe
        }

        ceo.setRol(com.restaurant.tec.entity.Role.CEO);
        ceo.setPassword(passwordEncoder.encode(ceo.getPassword()));
        ceo.setEnabled(true);
        ceo.setFechaCreacion(LocalDateTime.now());

        return ResponseEntity.ok(userRepository.save(ceo));
    }

    // Eliminar o desactivar usuario
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
