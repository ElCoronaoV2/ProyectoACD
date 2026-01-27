#!/bin/bash

# Script para actualizar todos los imports después de la reorganización de paquetes

# Actualizar imports en todos los archivos Java
find /home/proyectoacd/ProyectoACD/tec/src/main/java -name "*.java" -type f -exec sed -i \
  -e 's/import com\.restaurant\.tec\.UserEntity;/import com.restaurant.tec.entity.UserEntity;/g' \
  -e 's/import com\.restaurant\.tec\.LocalEntity;/import com.restaurant.tec.entity.LocalEntity;/g' \
  -e 's/import com\.restaurant\.tec\.MenuEntity;/import com.restaurant.tec.entity.MenuEntity;/g' \
  -e 's/import com\.restaurant\.tec\.ReservaEntity;/import com.restaurant.tec.entity.ReservaEntity;/g' \
  -e 's/import com\.restaurant\.tec\.ResenaEntity;/import com.restaurant.tec.entity.ResenaEntity;/g' \
  -e 's/import com\.restaurant\.tec\.SugerenciaEntity;/import com.restaurant.tec.entity.SugerenciaEntity;/g' \
  -e 's/import com\.restaurant\.tec\.UserRepository;/import com.restaurant.tec.repository.UserRepository;/g' \
  -e 's/import com\.restaurant\.tec\.LocalRepository;/import com.restaurant.tec.repository.LocalRepository;/g' \
  -e 's/import com\.restaurant\.tec\.MenuRepository;/import com.restaurant.tec.repository.MenuRepository;/g' \
  -e 's/import com\.restaurant\.tec\.ReservaRepository;/import com.restaurant.tec.repository.ReservaRepository;/g' \
  -e 's/import com\.restaurant\.tec\.ResenaRepository;/import com.restaurant.tec.repository.ResenaRepository;/g' \
  -e 's/import com\.restaurant\.tec\.SugerenciaRepository;/import com.restaurant.tec.repository.SugerenciaRepository;/g' \
  {} +

echo "Imports actualizados exitosamente"
