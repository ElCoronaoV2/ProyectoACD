package com.restaurant.tec.controller;

import com.restaurant.tec.entity.LocalEntity;
import com.restaurant.tec.entity.MenuEntity;
import com.restaurant.tec.entity.UserEntity;
import com.restaurant.tec.repository.LocalRepository;
import com.restaurant.tec.repository.MenuRepository;
import com.restaurant.tec.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/management")
public class ManagementController {

    @Autowired
    private LocalRepository localRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Obtener restaurantes del CEO autenticado
    @GetMapping("/restaurantes")
    public ResponseEntity<List<LocalEntity>> getMisRestaurantes(Authentication authentication) {
        UserEntity ceo = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        // Si es DIRECTOR, devuélvele todos (opcional, o filtrar diferente)
        if (ceo.getRol() == com.restaurant.tec.entity.Role.DIRECTOR) {
            return ResponseEntity.ok(localRepository.findAll());
        }

        return ResponseEntity.ok(ceo.getRestaurantesPropios());
    }

    // Crear nuevo restaurante
    @PostMapping("/restaurantes")
    public ResponseEntity<LocalEntity> createRestaurante(@RequestBody LocalEntity local,
            Authentication authentication) {
        UserEntity ceo = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        local.setPropietario(ceo);
        // Valores por defecto
        if (local.getValoracion() == null)
            local.setValoracion(0.0);

        return ResponseEntity.ok(localRepository.save(local));
    }

    // Crear empleado para un restaurante
    @PostMapping("/restaurantes/{localId}/empleados")
    public ResponseEntity<?> createEmpleado(@PathVariable Long localId, @RequestBody UserEntity empleado,
            Authentication authentication) {
        UserEntity ceo = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        LocalEntity local = localRepository.findById(localId)
                .orElseThrow(() -> new RuntimeException("Restaurante no encontrado"));

        // Verificar que el restaurante pertenece al CEO
        if (!local.getPropietario().getId().equals(ceo.getId())
                && ceo.getRol() != com.restaurant.tec.entity.Role.DIRECTOR) {
            return ResponseEntity.status(403).body("No tienes permiso sobre este restaurante");
        }

        // Configurar empleado
        empleado.setRol(com.restaurant.tec.entity.Role.EMPLEADO);
        empleado.setPassword(passwordEncoder.encode(empleado.getPassword()));
        empleado.setRestauranteTrabajo(local);
        empleado.setEnabled(true);
        empleado.setFechaCreacion(LocalDateTime.now());

        return ResponseEntity.ok(userRepository.save(empleado));
    }

    // Gestión de Menús (CEO)
    @PostMapping("/restaurantes/{localId}/menus")
    public ResponseEntity<?> createMenu(@PathVariable Long localId, @RequestBody MenuEntity menu,
            Authentication authentication) {
        UserEntity ceo = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        LocalEntity local = localRepository.findById(localId)
                .orElseThrow(() -> new RuntimeException("Restaurante no encontrado"));

        if (!local.getPropietario().getId().equals(ceo.getId())
                && ceo.getRol() != com.restaurant.tec.entity.Role.DIRECTOR) {
            return ResponseEntity.status(403).body("No tienes permiso sobre este restaurante");
        }

        menu.setLocal(local);
        return ResponseEntity.ok(menuRepository.save(menu));
    }

    @DeleteMapping("/menus/{menuId}")
    public ResponseEntity<?> deleteMenu(@PathVariable Long menuId, Authentication authentication) {
        UserEntity ceo = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        MenuEntity menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new RuntimeException("Menú no encontrado"));

        if (!menu.getLocal().getPropietario().getId().equals(ceo.getId())
                && ceo.getRol() != com.restaurant.tec.entity.Role.DIRECTOR) {
            return ResponseEntity.status(403).body("No tienes permiso para eliminar este menú");
        }

        menuRepository.delete(menu);
        return ResponseEntity.ok().build();
    }
}
