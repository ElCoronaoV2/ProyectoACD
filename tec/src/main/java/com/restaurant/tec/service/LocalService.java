package com.restaurant.tec.service;

import com.restaurant.tec.dto.CreateLocalRequest;
import com.restaurant.tec.dto.LocalResponse;
import com.restaurant.tec.entity.LocalEntity;
import com.restaurant.tec.repository.LocalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LocalService {

    @Autowired
    private LocalRepository localRepository;

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
        local.setValoracion(request.getValoracion());

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
        local.setValoracion(request.getValoracion());

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
        return new LocalResponse(
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
    }
}
