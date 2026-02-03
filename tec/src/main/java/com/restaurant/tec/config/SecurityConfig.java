package com.restaurant.tec.config;

import com.restaurant.tec.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.context.annotation.Lazy;
import org.springframework.beans.factory.annotation.Qualifier;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    @Lazy
    @Qualifier("customUserDetailsService")
    private UserDetailsService databaseUserDetailsService;

    @Value("${api.admin.username:admin}")
    private String apiAdminUsername;

    @Value("${api.admin.password:restauranttec2026}")
    private String apiAdminPassword;

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(compositeUserDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    /**
     * UserDetailsService compuesto: primero busca en admin en memoria, luego en BD
     */
    @Bean
    @Primary
    public UserDetailsService compositeUserDetailsService() {
        // Usuario admin en memoria para acceso Basic Auth al API
        UserDetails adminUser = User.builder()
                .username(apiAdminUsername)
                .password(passwordEncoder().encode(apiAdminPassword))
                .roles("DIRECTOR", "ADMIN", "API_ACCESS")
                .build();
        InMemoryUserDetailsManager inMemoryManager = new InMemoryUserDetailsManager(adminUser);

        return username -> {
            // Primero buscar en memoria (admin)
            try {
                return inMemoryManager.loadUserByUsername(username);
            } catch (UsernameNotFoundException e) {
                // Si no está en memoria, buscar en BD
                return databaseUserDetailsService.loadUserByUsername(username);
            }
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authenticationProvider(authenticationProvider())
                .cors(org.springframework.security.config.Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Endpoints públicos
                        .requestMatchers(HttpMethod.GET, "/api/locales/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/forgot-password", "/api/auth/reset-password")
                        .permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/auth/verify").permitAll()
                        // El endpoint /api (raíz) requiere autenticación
                        .requestMatchers("/api", "/api/").authenticated()

                        // Endpoints de gestión (Director)
                        .requestMatchers("/api/admin/**").hasRole("DIRECTOR")

                        // Endpoints de gestión (CEO)
                        .requestMatchers("/api/management/**").hasAnyRole("DIRECTOR", "CEO")

                        // Endpoints de empleado
                        .requestMatchers("/api/staff/**").hasAnyRole("DIRECTOR", "CEO", "EMPLEADO")

                        // Todo lo demás requiere autenticación
                        .anyRequest().authenticated())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Basic Auth como fallback para acceso desde navegador
                .httpBasic(org.springframework.security.config.Customizer.withDefaults());

        // JWT filter tiene prioridad - si hay token JWT válido, lo usa
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
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
