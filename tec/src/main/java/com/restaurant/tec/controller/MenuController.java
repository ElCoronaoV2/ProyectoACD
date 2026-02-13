package com.restaurant.tec.controller;

import com.restaurant.tec.dto.MenuRequest;
import com.restaurant.tec.entity.MenuEntity;
import com.restaurant.tec.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

/**
 * Controlador REST para gestionar menús de restaurantes.
 * Proporciona endpoints administrativos para crear, actualizar, eliminar y consultar menús.
 * 
 * @author RestaurantTec Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/admin/menus")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @PostMapping
    public ResponseEntity<MenuEntity> createMenu(@Valid @RequestBody MenuRequest request) {
        return ResponseEntity.ok(menuService.createMenu(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MenuEntity> updateMenu(@PathVariable Long id, @Valid @RequestBody MenuRequest request) {
        return ResponseEntity.ok(menuService.updateMenu(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMenu(@PathVariable Long id) {
        menuService.deleteMenu(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MenuEntity> getMenu(@PathVariable Long id) {
        return ResponseEntity.ok(menuService.getMenu(id));
    }
}
