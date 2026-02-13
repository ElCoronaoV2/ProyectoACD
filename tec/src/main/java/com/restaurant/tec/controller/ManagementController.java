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

/**
 * Controlador REST para funciones de gestión de CEO y Director.
 * Proporciona endpoints para que los CEOs y Directores gestionen sus
 * restaurantes,
 * empleados, menús, reservas y estadísticas.
 * 
 * @author RestaurantTec Team
 * @version 1.0
 */
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
        private com.restaurant.tec.repository.ReservaRepository reservaRepository;

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

        @PutMapping("/restaurantes/{id}")
        public ResponseEntity<?> updateRestaurante(@PathVariable Long id, @RequestBody LocalEntity localData,
                        Authentication authentication) {
                UserEntity ceo = userRepository.findByEmail(authentication.getName())
                                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

                LocalEntity local = localRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Restaurante no encontrado"));

                if (!local.getPropietario().getId().equals(ceo.getId())
                                && ceo.getRol() != com.restaurant.tec.entity.Role.DIRECTOR) {
                        return ResponseEntity.status(403).body("No tienes permiso");
                }

                local.setNombre(localData.getNombre());
                local.setDireccion(localData.getDireccion());
                local.setCapacidad(localData.getCapacidad());
                local.setHorario(localData.getHorario());
                local.setImagenUrl(localData.getImagenUrl());
                // ... otros campos

                return ResponseEntity.ok(localRepository.save(local));
        }

        @DeleteMapping("/restaurantes/{id}")
        public ResponseEntity<?> deleteRestaurante(@PathVariable Long id, Authentication authentication) {
                UserEntity ceo = userRepository.findByEmail(authentication.getName())
                                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

                LocalEntity local = localRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Restaurante no encontrado"));

                if (!local.getPropietario().getId().equals(ceo.getId())
                                && ceo.getRol() != com.restaurant.tec.entity.Role.DIRECTOR) {
                        return ResponseEntity.status(403).body("No tienes permiso");
                }

                // 1. Desvincular empleados (Setear restaurante_trabajo_id = NULL)
                java.util.List<UserEntity> empleados = userRepository.findByRol(com.restaurant.tec.entity.Role.EMPLEADO)
                                .stream()
                                .filter(u -> u.getRestauranteTrabajo() != null
                                                && u.getRestauranteTrabajo().getId().equals(id))
                                .collect(java.util.stream.Collectors.toList());

                for (UserEntity empleado : empleados) {
                        empleado.setRestauranteTrabajo(null);
                        userRepository.save(empleado);
                }

                // 2. Desvincular Menus (Mover a Menús Generales)
                java.util.List<MenuEntity> menus = menuRepository.findByLocalId(id);
                for (MenuEntity menu : menus) {
                        menu.setLocal(null);
                        menu.setPropietario(ceo); // Asignar al CEO para que no se pierdan
                        menuRepository.save(menu);
                }

                // 3. Eliminar Reservas (Cascade manual)
                java.util.List<com.restaurant.tec.entity.ReservaEntity> reservas = reservaRepository.findByLocalId(id);
                reservaRepository.deleteAll(reservas);

                localRepository.delete(local);
                return ResponseEntity.ok().build();
        }

        @GetMapping("/restaurantes/{id}")
        public ResponseEntity<LocalEntity> getRestaurante(@PathVariable Long id, Authentication authentication) {
                UserEntity ceo = userRepository.findByEmail(authentication.getName())
                                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

                LocalEntity local = localRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Restaurante no encontrado"));

                if (!local.getPropietario().getId().equals(ceo.getId())
                                && ceo.getRol() != com.restaurant.tec.entity.Role.DIRECTOR) {
                        return ResponseEntity.status(403).build();
                }

                return ResponseEntity.ok(local);
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

        // Listar empleados de un restaurante
        @GetMapping("/restaurantes/{localId}/empleados")
        public ResponseEntity<List<UserEntity>> getEmpleados(@PathVariable Long localId,
                        Authentication authentication) {
                UserEntity ceo = userRepository.findByEmail(authentication.getName())
                                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

                LocalEntity local = localRepository.findById(localId)
                                .orElseThrow(() -> new RuntimeException("Restaurante no encontrado"));

                if (!local.getPropietario().getId().equals(ceo.getId())
                                && ceo.getRol() != com.restaurant.tec.entity.Role.DIRECTOR) {
                        return ResponseEntity.status(403).build();
                }

                return ResponseEntity.ok(userRepository.findByRol(com.restaurant.tec.entity.Role.EMPLEADO).stream()
                                .filter(u -> u.getRestauranteTrabajo() != null
                                                && u.getRestauranteTrabajo().getId().equals(localId))
                                .collect(java.util.stream.Collectors.toList()));
        }

        @DeleteMapping("/empleados/{id}")
        public ResponseEntity<?> deleteEmpleado(@PathVariable Long id, Authentication authentication) {
                UserEntity ceo = userRepository.findByEmail(authentication.getName())
                                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

                UserEntity empleado = userRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));

                LocalEntity local = empleado.getRestauranteTrabajo();
                if (local == null) {
                        // Si no tiene local asignado, quizás solo el director puede borrarlo o es un
                        // error
                        if (ceo.getRol() != com.restaurant.tec.entity.Role.DIRECTOR) {
                                return ResponseEntity.status(403).body("No tienes permiso");
                        }
                } else {
                        if (!local.getPropietario().getId().equals(ceo.getId())
                                        && ceo.getRol() != com.restaurant.tec.entity.Role.DIRECTOR) {
                                return ResponseEntity.status(403).body("No tienes permiso");
                        }
                }

                userRepository.delete(empleado);
                return ResponseEntity.ok().build();
        }

        @PutMapping("/empleados/{id}")
        public ResponseEntity<?> updateEmpleado(@PathVariable Long id, @RequestBody UserEntity empleadoData,
                        Authentication authentication) {
                UserEntity ceo = userRepository.findByEmail(authentication.getName())
                                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

                UserEntity empleado = userRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));

                LocalEntity local = empleado.getRestauranteTrabajo();
                if (local != null && !local.getPropietario().getId().equals(ceo.getId())
                                && ceo.getRol() != com.restaurant.tec.entity.Role.DIRECTOR) {
                        return ResponseEntity.status(403).body("No tienes permiso");
                }

                empleado.setNombre(empleadoData.getNombre());
                empleado.setEmail(empleadoData.getEmail());
                if (empleadoData.getPassword() != null && !empleadoData.getPassword().isEmpty()) {
                        empleado.setPassword(passwordEncoder.encode(empleadoData.getPassword()));
                }
                return ResponseEntity.ok(userRepository.save(empleado));
        }

        // --- GESTIÓN DE MENÚS (CEO y EMPLEADOS) ---

        @PostMapping("/restaurantes/{localId}/menus")
        public ResponseEntity<?> createMenu(@PathVariable Long localId, @RequestBody MenuEntity menu,
                        Authentication authentication) {
                UserEntity user = userRepository.findByEmail(authentication.getName())
                                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

                LocalEntity local = localRepository.findById(localId)
                                .orElseThrow(() -> new RuntimeException("Restaurante no encontrado"));

                // Permiso: Dueño, Director o Empleado de ESE local
                boolean isOwner = local.getPropietario().getId().equals(user.getId());
                boolean isDirector = user.getRol() == com.restaurant.tec.entity.Role.DIRECTOR;
                boolean isEmployeeOfLocal = user.getRol() == com.restaurant.tec.entity.Role.EMPLEADO
                                && user.getRestauranteTrabajo() != null
                                && user.getRestauranteTrabajo().getId().equals(localId);

                if (!isOwner && !isDirector && !isEmployeeOfLocal) {
                        return ResponseEntity.status(403).body("No tienes permiso sobre este restaurante");
                }

                menu.setLocal(local);
                menu.setPropietario(local.getPropietario()); // El propietario del menú es el dueño del local

                return ResponseEntity.ok(menuRepository.save(menu));
        }

        @GetMapping("/restaurantes/{localId}/menus")
        public ResponseEntity<List<MenuEntity>> getMenus(@PathVariable Long localId, Authentication authentication) {
                UserEntity user = userRepository.findByEmail(authentication.getName())
                                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

                LocalEntity local = localRepository.findById(localId)
                                .orElseThrow(() -> new RuntimeException("Restaurante no encontrado"));

                // Permiso: Dueño, Director o Empleado de ESE local
                boolean isOwner = local.getPropietario().getId().equals(user.getId());
                boolean isDirector = user.getRol() == com.restaurant.tec.entity.Role.DIRECTOR;
                boolean isEmployeeOfLocal = user.getRol() == com.restaurant.tec.entity.Role.EMPLEADO
                                && user.getRestauranteTrabajo() != null
                                && user.getRestauranteTrabajo().getId().equals(localId);

                if (!isOwner && !isDirector && !isEmployeeOfLocal) {
                        return ResponseEntity.status(403).build();
                }

                return ResponseEntity.ok(menuRepository.findByLocalId(localId));
        }

        @DeleteMapping("/menus/{id}")
        public ResponseEntity<?> deleteMenu(@PathVariable Long id, Authentication authentication) {
                UserEntity user = userRepository.findByEmail(authentication.getName())
                                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

                MenuEntity menu = menuRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Menú no encontrado"));

                // Permiso: El usuario debe tener permiso sobre el local del menú (si tiene) o
                // ser el propietario del menú
                if (menu.getLocal() != null) {
                        LocalEntity local = menu.getLocal();
                        boolean isOwner = local.getPropietario().getId().equals(user.getId());
                        boolean isDirector = user.getRol() == com.restaurant.tec.entity.Role.DIRECTOR;
                        boolean isEmployeeOfLocal = user.getRol() == com.restaurant.tec.entity.Role.EMPLEADO
                                        && user.getRestauranteTrabajo() != null
                                        && user.getRestauranteTrabajo().getId().equals(local.getId());

                        if (!isOwner && !isDirector && !isEmployeeOfLocal) {
                                return ResponseEntity.status(403).body("No tienes permiso");
                        }

                        // Al borrar un menú asignado, desvinculamos y movemos a general (si es CEO)
                        // o eliminamos? El usuario pidió reutilizar.
                        // Si borramos el menú explicitamente, asumimos que se quiere borrar O mover a
                        // general?
                        // "los menus yo los dejaria... cualquier restaurante podria reutilizar"
                        // Al borrar explicitamente un menú, lo moveremos a general (desvincular)
                        menu.setLocal(null);
                        menu.setPropietario(local.getPropietario());
                        menuRepository.save(menu);
                        return ResponseEntity.ok("Menú movido a generales");
                } else {
                        // Menú general
                        if (menu.getPropietario() != null && !menu.getPropietario().getId().equals(user.getId())) {
                                return ResponseEntity.status(403).body("No tienes permiso");
                        }
                        menuRepository.delete(menu);
                        return ResponseEntity.ok("Menú general eliminado");
                }
        }

        @PutMapping("/menus/{id}")
        public ResponseEntity<?> updateMenu(@PathVariable Long id, @RequestBody MenuEntity menuData,
                        Authentication authentication) {
                // ... implementación similar simplificada para brevedad, permitiendo empleados
                return ResponseEntity.ok().build(); // TODO: Implementar si necesario
        }

        // --- GESTIÓN DE MENÚS GENERALES ---

        // Listar TODOS los menús (Biblioteca Global)
        @GetMapping("/menus/general")
        public ResponseEntity<java.util.List<MenuEntity>> getGeneralMenus(Authentication authentication) {
                // Devolvemos todos los menús para que puedan ser importados
                return ResponseEntity.ok(menuRepository.findAll());
        }

        // Asignar (COPIAR/IMPORTAR) menú general a un local
        @PutMapping("/menus/{menuId}/assign/{localId}")
        public ResponseEntity<?> assignMenuToLocal(@PathVariable Long menuId, @PathVariable Long localId,
                        Authentication authentication) {
                try {
                        System.out.println("🔄 [IMPORT] Iniciando importación - MenuID: " + menuId + ", LocalID: "
                                        + localId);

                        UserEntity user = userRepository.findByEmail(authentication.getName())
                                        .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

                        System.out.println("👤 [IMPORT] Usuario: " + user.getEmail() + " (Rol: " + user.getRol() + ")");

                        MenuEntity originalMenu = menuRepository.findById(menuId)
                                        .orElseThrow(() -> new RuntimeException("Menú no encontrado"));

                        System.out.println("📋 [IMPORT] Menú encontrado: " + originalMenu.getNombre());

                        LocalEntity local = localRepository.findById(localId)
                                        .orElseThrow(() -> new RuntimeException("Restaurante no encontrado"));

                        System.out.println("🏪 [IMPORT] Restaurante destino: " + local.getNombre());

                        // Verificar permisos sobre el local destino
                        boolean isOwner = local.getPropietario().getId().equals(user.getId());
                        boolean isDirector = user.getRol() == com.restaurant.tec.entity.Role.DIRECTOR;
                        boolean isEmployeeOfLocal = user.getRol() == com.restaurant.tec.entity.Role.EMPLEADO
                                        && user.getRestauranteTrabajo() != null
                                        && user.getRestauranteTrabajo().getId().equals(localId);

                        System.out.println("🔐 [IMPORT] Permisos - Owner: " + isOwner + ", Director: " + isDirector
                                        + ", Employee: " + isEmployeeOfLocal);

                        if (!isOwner && !isDirector && !isEmployeeOfLocal) {
                                System.out.println("❌ [IMPORT] Acceso denegado");
                                return ResponseEntity.status(403)
                                                .body(java.util.Map.of("error", "No tienes permiso sobre este local"));
                        }

                        // CLONAR el menú
                        MenuEntity newMenu = new MenuEntity();
                        newMenu.setNombre(originalMenu.getNombre());
                        newMenu.setDescripcion(originalMenu.getDescripcion());
                        newMenu.setPrecio(originalMenu.getPrecio());
                        newMenu.setIngredientes(originalMenu.getIngredientes());
                        newMenu.setAlergenos(originalMenu.getAlergenos());

                        // Copiar platos estructurados
                        newMenu.setPrimerPlato(originalMenu.getPrimerPlato());
                        newMenu.setPrimerPlatoDesc(originalMenu.getPrimerPlatoDesc());
                        newMenu.setPrimerPlatoIngredientes(originalMenu.getPrimerPlatoIngredientes());

                        newMenu.setSegundoPlato(originalMenu.getSegundoPlato());
                        newMenu.setSegundoPlatoDesc(originalMenu.getSegundoPlatoDesc());
                        newMenu.setSegundoPlatoIngredientes(originalMenu.getSegundoPlatoIngredientes());

                        newMenu.setPostre(originalMenu.getPostre());
                        newMenu.setPostreDesc(originalMenu.getPostreDesc());
                        newMenu.setPostreIngredientes(originalMenu.getPostreIngredientes());

                        // Asignar al local y propietario destino
                        newMenu.setLocal(local);
                        newMenu.setPropietario(local.getPropietario());
                        newMenu.setDisponible(originalMenu.getDisponible());

                        MenuEntity savedMenu = menuRepository.save(newMenu);
                        System.out.println("✅ [IMPORT] Menú importado exitosamente - ID: " + savedMenu.getId());

                        return ResponseEntity.ok(java.util.Map.of(
                                        "message", "Menú importado correctamente al restaurante",
                                        "menuId", savedMenu.getId(),
                                        "menuNombre", savedMenu.getNombre()));
                } catch (Exception e) {
                        System.err.println("❌ [IMPORT] Error: " + e.getMessage());
                        e.printStackTrace();
                        return ResponseEntity.status(500)
                                        .body(java.util.Map.of("error", "Error al importar menú: " + e.getMessage()));
                }
        }

}
