package com.restaurant.tec.config;

import com.restaurant.tec.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    // La inicialización de datos se realiza ahora mediante data.sql
    // Este bean se mantiene por si se necesita lógica compleja de inicialización en
    // el futuro
    @Bean
    public CommandLineRunner initData(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            System.out.println("🚀 Aplicación iniciada. Los datos se cargan desde data.sql si es necesario.");
        };
    }
}
