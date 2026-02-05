package com.restaurant.tec.controller;

import com.restaurant.tec.entity.UserEntity;
import com.restaurant.tec.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    // @Autowired
    // private PasswordEncoder passwordEncoder; // Removed as it is handled in
    // UserService now

    @Autowired
    private com.restaurant.tec.service.OnlineStatusService onlineStatusService;

    // Listar todos los CEOs
    @GetMapping("/ceos")
    public ResponseEntity<List<UserEntity>> getAllCeos() {
        return ResponseEntity.ok(userRepository.findByRol(com.restaurant.tec.entity.Role.CEO));
    }

    // Listar usuarios (con filtro opcional por rol)
    @GetMapping("/users")
    public ResponseEntity<List<UserEntity>> getAllUsers(
            @RequestParam(required = false) String role) {
        System.out.println("DEBUG: getAllUsers called with role: " + role);
        if (role != null && !role.trim().isEmpty()) {
            try {
                com.restaurant.tec.entity.Role roleEnum = com.restaurant.tec.entity.Role.valueOf(role.toUpperCase());
                return ResponseEntity.ok(userRepository.findByRol(roleEnum));
            } catch (IllegalArgumentException e) {
                // Si el rol no es válido, devolvemos todo o lista vacía?
                // Mejor ignorar filtro inválido y devolver todo, o devolver 400 explícito.
                // Para depuración, vamos a devolver todo si el rol está mal o logear.
                // Pero lo correcto sería 400 si el rol no existe.
                // El error original era 400 automatico.
                // Asumiremos que si viene cadena vacía "" es 'todos'.
                return ResponseEntity.ok(userRepository.findAll());
            }
        }
        return ResponseEntity.ok(userRepository.findAll());
    }

    // Estadísticas del Dashboard
    @GetMapping("/stats")
    public ResponseEntity<com.restaurant.tec.dto.DashboardStatsDTO> getDashboardStats() {
        long totalUsers = userRepository.count();
        long usersOnline = onlineStatusService.getOnlineUserCount();
        long guestsOnline = onlineStatusService.getOnlineGuestCount();
        long unverifiedUsers = userRepository.countByEnabled(false);

        java.util.Map<String, Long> rolesCount = new java.util.HashMap<>();
        rolesCount.put("DIRECTOR", userRepository.countByRol(com.restaurant.tec.entity.Role.DIRECTOR));
        rolesCount.put("CEO", userRepository.countByRol(com.restaurant.tec.entity.Role.CEO));
        rolesCount.put("EMPLEADO", userRepository.countByRol(com.restaurant.tec.entity.Role.EMPLEADO));
        rolesCount.put("USER", userRepository.countByRol(com.restaurant.tec.entity.Role.USER));

        return ResponseEntity.ok(new com.restaurant.tec.dto.DashboardStatsDTO(
                totalUsers, usersOnline, guestsOnline, unverifiedUsers, rolesCount));
    }

    @Autowired
    private com.restaurant.tec.service.UserService userService;

    // Crear cualquier usuario (CEO, Empleado, etc.)
    @PostMapping("/users")
    public ResponseEntity<UserEntity> createUser(@RequestBody com.restaurant.tec.dto.RegisterRequest request) {
        try {
            UserEntity createdUser = userService.registerUser(request);
            return ResponseEntity.ok(createdUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Eliminar o desactivar usuario
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
