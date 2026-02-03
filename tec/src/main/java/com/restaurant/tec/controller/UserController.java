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
    public ResponseEntity<UserEntity> getProfile(Authentication authentication) {
        UserEntity user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        return ResponseEntity.ok(user);
    }

    @PutMapping("/profile")
    public ResponseEntity<UserEntity> updateProfile(@RequestBody Map<String, Object> updates,
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

        // Save
        // We can use UserService or Repository directly if logic is simple.
        // Assuming UserService has save method or we use Repository.
        // Step 35 showed UserService class. I didn't see a generic 'update' method but
        // it might exist or I can use Repo.
        // Safest is to use Repo for now or invoke UserService if I added update logic.
        // I'll use repository for simplicity here as UserService might be for Auth.
        // Wait, Step 35 showed UserService has registerUser, verifyUser etc.
        // I'll use repository.

        return ResponseEntity.ok(userRepository.save(user));
    }
}
