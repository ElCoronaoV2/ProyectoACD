package com.restaurant.tec.controller;

import com.restaurant.tec.entity.LocalEntity;
import com.restaurant.tec.entity.UserEntity;
import com.restaurant.tec.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/staff")
public class StaffController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/my-restaurant")
    public ResponseEntity<LocalEntity> getMyRestaurant(Authentication authentication) {
        UserEntity employee = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        if (employee.getRestauranteTrabajo() == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(employee.getRestauranteTrabajo());
    }
}
