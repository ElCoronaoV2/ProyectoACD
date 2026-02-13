package com.restaurant.tec.controller;

import com.restaurant.tec.entity.UserEntity;
import com.restaurant.tec.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

/**
 * Controlador REST para funciones administrativas del sistema.
 * Proporciona endpoints para gestión de usuarios, CEOs y estadísticas del dashboard.
 * 
 * @author RestaurantTec Team
 * @version 1.0
 */
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

    /**
     * Obtiene la lista de todos los usuarios con rol CEO.
     * 
     * @return ResponseEntity con lista de usuarios CEO
     */
    @GetMapping("/ceos")
    public ResponseEntity<List<UserEntity>> getAllCeos() {
        return ResponseEntity.ok(userRepository.findByRol(com.restaurant.tec.entity.Role.CEO));
    }

    /**
     * Obtiene la lista de todos los usuarios del sistema.
     * Puede filtrar por rol específico.
     * 
     * @param role rol opcional para filtrar (USER, CEO, DIRECTOR, EMPLEADO, ADMIN)
     * @return ResponseEntity con lista de usuarios
     */
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

    /**
     * Obtiene estadísticas del dashboard administrativo.
     * Incluye total de usuarios, usuarios online, invitados online, usuarios sin verificar
     * y distribución por roles.
     * 
     * @return ResponseEntity con objeto DashboardStatsDTO
     */
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

    /**
     * Crea un nuevo usuario con cualquier rol.
     * 
     * @param request datos de registro con rol especificado
     * @return ResponseEntity con el usuario creado
     */
    @PostMapping("/users")
    public ResponseEntity<UserEntity> createUser(@Valid @RequestBody com.restaurant.tec.dto.RegisterRequest request) {
        try {
            UserEntity createdUser = userService.registerUser(request);
            return ResponseEntity.ok(createdUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Elimina un usuario del sistema.
     * 
     * @param id ID del usuario a eliminar
     * @return ResponseEntity vacío con estado 200
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @Autowired
    private com.restaurant.tec.repository.LocalRepository localRepository;

    /**
     * Actualiza los datos de un usuario.
     * Permite actualizar nombre, email, teléfono, rol y restaurante de trabajo.
     * 
     * @param id ID del usuario a actualizar
     * @param updates mapa con los campos a actualizar
     * @return ResponseEntity con el usuario actualizado o error
     */
    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody java.util.Map<String, Object> updates) {
        return userRepository.findById(id).map(user -> {
            if (updates.containsKey("nombre"))
                user.setNombre((String) updates.get("nombre"));
            if (updates.containsKey("email"))
                user.setEmail((String) updates.get("email"));
            if (updates.containsKey("telefono"))
                user.setTelefono((String) updates.get("telefono"));
            if (updates.containsKey("alergenos"))
                user.setAlergenos((String) updates.get("alergenos"));
            if (updates.containsKey("enabled"))
                user.setEnabled((Boolean) updates.get("enabled"));
            if (updates.containsKey("rol")) {
                try {
                    com.restaurant.tec.entity.Role newRol = com.restaurant.tec.entity.Role
                            .valueOf((String) updates.get("rol"));
                    // No permitir crear DIRECTOR desde aquí
                    if (newRol != com.restaurant.tec.entity.Role.DIRECTOR) {
                        user.setRol(newRol);
                    }
                } catch (IllegalArgumentException ignored) {
                }
            }
            // Empleado: asignar un solo restaurante de trabajo
            if (updates.containsKey("restauranteTrabajoId")) {
                Object rtId = updates.get("restauranteTrabajoId");
                if (rtId != null) {
                    Long restauranteId = Long.valueOf(rtId.toString());
                    localRepository.findById(restauranteId).ifPresent(user::setRestauranteTrabajo);
                } else {
                    user.setRestauranteTrabajo(null);
                }
            }
            UserEntity savedUser = userRepository.save(user);

            // CEO: asignar restaurantes propios (actualizar propietario en locales)
            if (updates.containsKey("restauranteIds")) {
                @SuppressWarnings("unchecked")
                java.util.List<Object> ids = (java.util.List<Object>) updates.get("restauranteIds");
                // Quitar propiedad actual de este CEO
                java.util.List<com.restaurant.tec.entity.LocalEntity> owned = localRepository
                        .findByPropietarioId(savedUser.getId());
                for (com.restaurant.tec.entity.LocalEntity local : owned) {
                    local.setPropietario(null);
                    localRepository.save(local);
                }
                // Asignar nuevos
                if (ids != null) {
                    for (Object rid : ids) {
                        Long localId = Long.valueOf(rid.toString());
                        localRepository.findById(localId).ifPresent(local -> {
                            local.setPropietario(savedUser);
                            localRepository.save(local);
                        });
                    }
                }
            }

            return ResponseEntity.ok(savedUser);
        }).orElse(ResponseEntity.notFound().build());
    }
}
