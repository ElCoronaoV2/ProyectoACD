package com.restaurant.tec.config;

import com.restaurant.tec.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Arrays;
import org.springframework.security.config.Customizer;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private UserDetailsService customUserDetailsService;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // CSRF: Habilitado para POST/PUT/DELETE, pero deshabilitado para JWT stateless
                // Los endpoints POST/PUT/DELETE requieren token JWT en lugar de CSRF token
                .csrf(csrf -> csrf.disable()) // Stateless API con JWT, CSRF no es necesario
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Endpoints Públicos
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/locales/**").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/").permitAll()

                        // Gestión (Director/CEO)
                        .requestMatchers("/api/admin/menus/**").authenticated() // Fix 401/403
                        .requestMatchers("/api/admin/ai/**").authenticated()
                        .requestMatchers("/api/admin/locales/**").hasAnyRole("DIRECTOR", "CEO") // Schedule endpoints
                        .requestMatchers("/api/admin/**").hasAnyRole("DIRECTOR", "CEO") // Allow CEO too
                        .requestMatchers("/api/management/**").authenticated() // Fix 401/403

                        // Empleados
                        .requestMatchers("/api/staff/**").hasAnyRole("DIRECTOR", "CEO", "EMPLEADO")

                        // Usuarios Generales
                        .requestMatchers("/api/users/**").authenticated()
                        .requestMatchers("/api/reservas/mis-reservas").authenticated()
                        .requestMatchers("/api/reservas/availability").permitAll()
                        .requestMatchers("/api/reservas/create-payment-intent").permitAll() // Allow guests to create payment
                        .requestMatchers("/api/reservas/guest").permitAll() // Allow guest reservations
                        .requestMatchers("/api/reservas/local/**").hasAnyRole("DIRECTOR", "CEO", "EMPLEADO")
                        .requestMatchers("/api/reservas/upcoming", "/api/reservas/unconfirmed-urgent")
                        .hasRole("DIRECTOR")
                        .requestMatchers("/api/reservas/**").authenticated()
                        .requestMatchers("/api").authenticated() // Raíz API

                        .anyRequest().authenticated())
                .headers(headers -> headers
                        .frameOptions(frame -> frame.deny())
                        .contentTypeOptions(content -> {
                        }))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .httpBasic(Customizer.withDefaults()); // Habilitar Basic Auth como solicitado

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
                "https://restaurant-tec.es",
                "https://www.restaurant-tec.es",
                "https://api.restaurant-tec.es",
                "http://localhost:4200"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
