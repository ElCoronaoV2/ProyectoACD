package com.restaurant.tec.service;

import com.restaurant.tec.dto.CreateLocalRequest;
import com.restaurant.tec.dto.LocalResponse;
import com.restaurant.tec.entity.LocalEntity;
import com.restaurant.tec.entity.ResenaEntity;
import com.restaurant.tec.entity.UserEntity;
import com.restaurant.tec.repository.LocalRepository;
import com.restaurant.tec.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LocalService {

    @Autowired
    private LocalRepository localRepository;

    @Autowired
    private com.restaurant.tec.repository.ResenaRepository resenaRepository;

    @Autowired
    private UserRepository userRepository;

    // Crear un nuevo local
    @Transactional
    public LocalResponse crearLocal(CreateLocalRequest request) {
        LocalEntity local = new LocalEntity();
        local.setNombre(request.getNombre());
        local.setDireccion(request.getDireccion());
        local.setCapacidad(request.getCapacidad());
        local.setHorario(request.getHorario());
        local.setLatitud(request.getLatitud());
        local.setLongitud(request.getLongitud());
        local.setPosX(request.getPosX());
        local.setPosY(request.getPosY());
        local.setImagenUrl(request.getImagenUrl());
        // La valoración inicial es calculada (0 si no hay reseñas)

        // Shift Hours
        local.setAperturaComida(request.getAperturaComida());
        local.setCierreComida(request.getCierreComida());
        local.setAperturaCena(request.getAperturaCena());
        local.setCierreCena(request.getCierreCena());

        LocalEntity savedLocal = localRepository.save(local);
        return convertirAResponse(savedLocal);
    }

    // Obtener todos los locales
    public List<LocalResponse> obtenerTodosLosLocales() {
        return localRepository.findAll().stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    // Obtener un local por ID
    public LocalResponse obtenerLocalPorId(Long id) {
        LocalEntity local = localRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Local no encontrado con ID: " + id));
        return convertirAResponse(local);
    }

    // Actualizar un local
    @Transactional
    public LocalResponse actualizarLocal(Long id, CreateLocalRequest request) {
        LocalEntity local = localRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Local no encontrado con ID: " + id));

        local.setNombre(request.getNombre());
        local.setDireccion(request.getDireccion());
        local.setCapacidad(request.getCapacidad());
        local.setHorario(request.getHorario());
        local.setLatitud(request.getLatitud());
        local.setLongitud(request.getLongitud());
        local.setPosX(request.getPosX());
        local.setPosY(request.getPosY());
        local.setImagenUrl(request.getImagenUrl());
        // No actualizamos valoración directa, se recalcula

        // Shift Hours
        local.setAperturaComida(request.getAperturaComida());
        local.setCierreComida(request.getCierreComida());
        local.setAperturaCena(request.getAperturaCena());
        local.setCierreCena(request.getCierreCena());

        LocalEntity updatedLocal = localRepository.save(local);
        return convertirAResponse(updatedLocal);
    }

    // Eliminar un local
    @Transactional
    public void eliminarLocal(Long id) {
        if (!localRepository.existsById(id)) {
            throw new RuntimeException("Local no encontrado con ID: " + id);
        }
        localRepository.deleteById(id);
    }

    // Método auxiliar para convertir Entity a Response
    private LocalResponse convertirAResponse(LocalEntity local) {
        Double promedio = resenaRepository.calcularPuntuacionPromedio(local.getId());
        local.setValoracion(promedio != null ? promedio : 0.0);

        LocalResponse response = new LocalResponse(
                local.getId(),
                local.getNombre(),
                local.getDireccion(),
                local.getCapacidad(),
                local.getHorario(),
                local.getLatitud(),
                local.getLongitud(),
                local.getPosX(),
                local.getPosY(),
                local.getImagenUrl(),
                local.getValoracion());

        response.setAperturaComida(local.getAperturaComida());
        response.setCierreComida(local.getCierreComida());
        response.setAperturaCena(local.getAperturaCena());
        response.setCierreCena(local.getCierreCena());

        return response;
    }

    // Añadir una valoración al restaurante
    @Transactional
    public void addReview(Long localId, Integer puntuacion, String comentario) {
        // Obtener el local
        LocalEntity local = localRepository.findById(localId)
                .orElseThrow(() -> new RuntimeException("Local no encontrado con ID: " + localId));

        // Obtener el usuario autenticado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new RuntimeException("Debes iniciar sesión para valorar");
        }

        String email = authentication.getName();
        UserEntity usuario = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Crear la reseña
        ResenaEntity resena = new ResenaEntity(usuario, local, puntuacion, comentario);
        resenaRepository.save(resena);
    }

    public List<com.restaurant.tec.dto.ResenaResponse> getReviews(Long localId) {
        List<ResenaEntity> reviews = resenaRepository.findByLocalIdOrderByFechaCreacionDesc(localId);
        return reviews.stream().map(r -> new com.restaurant.tec.dto.ResenaResponse(
                r.getId(),
                r.getUsuario().getNombre(),
                r.getPuntuacion(),
                r.getComentario(),
                r.getFechaCreacion())).collect(Collectors.toList());
    }
}
