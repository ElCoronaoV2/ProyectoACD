package com.restaurant.tec.config;

import com.restaurant.tec.entity.UserEntity;
import com.restaurant.tec.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Verificar si existe el usuario admin
            if (userRepository.findByEmail("admin@restaurant.com").isEmpty()) {
                UserEntity admin = new UserEntity();
                admin.setEmail("admin@restaurant.com");
                admin.setPassword(passwordEncoder.encode("admin123")); // Contraseña por defecto
                admin.setNombre("Administrador");
                admin.setRol(com.restaurant.tec.entity.Role.DIRECTOR);
                admin.setTelefono("000000000");

                userRepository.save(admin);
                System.out.println("✅ Usuario Admin creado: admin@restaurant.com / admin123");
            }
        };
    }
}
