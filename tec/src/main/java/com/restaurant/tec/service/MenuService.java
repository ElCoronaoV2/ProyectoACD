package com.restaurant.tec.service;

import com.restaurant.tec.dto.MenuRequest;
import com.restaurant.tec.entity.LocalEntity;
import com.restaurant.tec.entity.MenuEntity;
import com.restaurant.tec.repository.LocalRepository;
import com.restaurant.tec.repository.MenuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MenuService {

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private LocalRepository localRepository;

    @Autowired
    private com.restaurant.tec.repository.MenuResenaRepository menuResenaRepository;

    @Transactional
    public MenuEntity createMenu(MenuRequest request) {
        LocalEntity local = localRepository.findById(request.getLocalId())
                .orElseThrow(() -> new RuntimeException("Restaurante no encontrado"));

        MenuEntity menu = new MenuEntity();
        mapRequestToEntity(request, menu);
        menu.setLocal(local);

        return menuRepository.save(menu);
    }

    @Transactional
    public MenuEntity updateMenu(Long id, MenuRequest request) {
        MenuEntity menu = menuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Menú no encontrado"));

        mapRequestToEntity(request, menu);

        return menuRepository.save(menu);
    }

    public void deleteMenu(Long id) {
        menuRepository.deleteById(id);
    }

    public MenuEntity getMenu(Long id) {
        MenuEntity menu = menuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Menú no encontrado"));

        Double avg = menuResenaRepository.getAverageRatingByMenuId(id);
        menu.setValoracionMedia(avg != null ? avg : 0.0);

        return menu;
    }

    public List<MenuEntity> getMenusByLocal(Long localId) {
        List<MenuEntity> menus = menuRepository.findByLocalId(localId);
        menus.forEach(menu -> {
            Double avg = menuResenaRepository.getAverageRatingByMenuId(menu.getId());
            menu.setValoracionMedia(avg != null ? avg : 0.0);
        });
        return menus;
    }

    private void mapRequestToEntity(MenuRequest request, MenuEntity menu) {
        menu.setNombre(request.getNombre());
        menu.setDescripcion(request.getDescripcion());
        menu.setPrecio(request.getPrecio());
        menu.setDisponible(request.getDisponible());
        menu.setAlergenos(request.getAlergenos());

        menu.setPrimerPlato(request.getPrimerPlato());
        menu.setPrimerPlatoDesc(request.getPrimerPlatoDesc());
        menu.setPrimerPlatoIngredientes(request.getPrimerPlatoIngredientes());

        menu.setSegundoPlato(request.getSegundoPlato());
        menu.setSegundoPlatoDesc(request.getSegundoPlatoDesc());
        menu.setSegundoPlatoIngredientes(request.getSegundoPlatoIngredientes());

        menu.setPostre(request.getPostre());
        menu.setPostreDesc(request.getPostreDesc());
        menu.setPostreIngredientes(request.getPostreIngredientes());
    }
}
