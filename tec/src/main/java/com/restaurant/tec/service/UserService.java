package com.restaurant.tec.service;

import com.restaurant.tec.entity.UserEntity;
import com.restaurant.tec.repository.UserRepository;
import com.restaurant.tec.dto.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public UserEntity registerUser(RegisterRequest request) {
        // Verificar si el email ya existe
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("El email ya está registrado");
        }

        UserEntity user = new UserEntity();
        user.setEmail(request.getEmail());
        // TEMPORAL: Sin encriptación para testing
        user.setPassword(request.getPassword());
        user.setNombre(request.getNombre());
        user.setTelefono(request.getTelefono());
        user.setAlergenos(request.getAlergenos());
        user.setRol("USER"); // Por defecto, todos son usuarios normales

        return userRepository.save(user);
    }

    public Optional<UserEntity> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional
    public UserEntity updateProfile(Long userId, String nombre, String telefono, String alergenos) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (nombre != null) user.setNombre(nombre);
        if (telefono != null) user.setTelefono(telefono);
        if (alergenos != null) user.setAlergenos(alergenos);

        return userRepository.save(user);
    }

    @Transactional
    public UserEntity updateAlergenos(Long userId, String alergenos) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        user.setAlergenos(alergenos);
        return userRepository.save(user);
    }
}
