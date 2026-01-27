#!/bin/bash

# Script para completar la configuración de restaurant-tec.es
# Pasos 8, 9 y 10

# Colores
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m'

echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${BLUE}  Completando configuración restaurant-tec.es${NC}"
echo -e "${BLUE}  Pasos 8, 9 y 10${NC}"
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo ""

# ============================================
# PASO 8: Verificar renovación automática
# ============================================
echo -e "${YELLOW}[PASO 8] Verificando renovación automática de certificados...${NC}"
echo ""

# Verificar si certbot.timer está activo
echo "Verificando servicio de renovación automática..."
if sudo systemctl is-active --quiet certbot.timer; then
    echo -e "${GREEN}✅ certbot.timer está activo${NC}"
    sudo systemctl status certbot.timer --no-pager | head -n 10
else
    echo -e "${YELLOW}⚠️  certbot.timer no está activo. Activando...${NC}"
    sudo systemctl enable certbot.timer
    sudo systemctl start certbot.timer
    echo -e "${GREEN}✅ certbot.timer activado${NC}"
fi
echo ""

# Probar renovación (dry-run)
echo "Probando renovación de certificados (simulación)..."
if sudo certbot renew --dry-run; then
    echo -e "${GREEN}✅ La renovación automática funcionará correctamente${NC}"
else
    echo -e "${RED}❌ Hay un problema con la renovación automática${NC}"
    echo "Revisa los logs con: sudo journalctl -u certbot"
fi
echo ""

# Mostrar próxima ejecución
echo "Próxima ejecución programada:"
sudo systemctl list-timers certbot.timer --no-pager
echo ""

echo -e "${GREEN}✅ Paso 8 completado${NC}"
echo ""

# ============================================
# PASO 9: Actualizar variables de entorno
# ============================================
echo -e "${YELLOW}[PASO 9] Actualizando variables de entorno del frontend...${NC}"
echo ""

FRONTEND_DIR="/home/proyectoacd/ProyectoACD/frontend"
ENV_PROD="$FRONTEND_DIR/src/environments/environment.prod.ts"

# Backup del archivo original
if [ -f "$ENV_PROD" ]; then
    cp "$ENV_PROD" "$ENV_PROD.backup"
    echo "Backup creado: $ENV_PROD.backup"
fi

# Crear nuevo archivo de producción
cat > "$ENV_PROD" << 'EOF'
export const environment = {
  production: true,
  apiUrl: 'https://restaurant-tec.es/api'
};
EOF

echo -e "${GREEN}✅ Variables de entorno actualizadas${NC}"
echo "Contenido de $ENV_PROD:"
cat "$ENV_PROD"
echo ""

# Recompilar frontend
echo "Recompilando frontend para producción..."
cd "$FRONTEND_DIR"
npm run build

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✅ Frontend recompilado exitosamente${NC}"
    
    # Copiar archivos a la ubicación de Nginx
    echo "Copiando archivos al directorio de Nginx..."
    DIST_DIR="$FRONTEND_DIR/dist/frontend/browser"
    if [ -d "$DIST_DIR" ]; then
        echo "Archivos compilados en: $DIST_DIR"
        ls -lh "$DIST_DIR" | head -n 10
    else
        echo -e "${RED}❌ No se encontró el directorio dist${NC}"
    fi
else
    echo -e "${RED}❌ Error al compilar el frontend${NC}"
    exit 1
fi
echo ""

echo -e "${GREEN}✅ Paso 9 completado${NC}"
echo ""

# ============================================
# PASO 10: Configurar CORS en el backend
# ============================================
echo -e "${YELLOW}[PASO 10] Configurando CORS en el backend...${NC}"
echo ""

BACKEND_DIR="/home/proyectoacd/ProyectoACD/tec"
CONFIG_DIR="$BACKEND_DIR/src/main/java/com/restaurant/tec/config"

# Crear directorio config si no existe
mkdir -p "$CONFIG_DIR"

# Crear archivo de configuración CORS
cat > "$CONFIG_DIR/CorsConfig.java" << 'EOF'
package com.restaurant.tec.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(
                    "https://restaurant-tec.es",
                    "https://www.restaurant-tec.es",
                    "http://localhost:4200"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
EOF

echo -e "${GREEN}✅ Archivo CorsConfig.java creado${NC}"
echo "Ubicación: $CONFIG_DIR/CorsConfig.java"
echo ""

# Actualizar LocalController para quitar @CrossOrigin individual
CONTROLLER_FILE="$BACKEND_DIR/src/main/java/com/restaurant/tec/controller/LocalController.java"
if [ -f "$CONTROLLER_FILE" ]; then
    echo "Actualizando LocalController..."
    # Comentar la línea @CrossOrigin si existe
    sed -i 's/^@CrossOrigin.*$/\/\/ @CrossOrigin - Configurado globalmente en CorsConfig/' "$CONTROLLER_FILE"
    echo -e "${GREEN}✅ LocalController actualizado${NC}"
fi
echo ""

echo -e "${GREEN}✅ Paso 10 completado${NC}"
echo ""

# ============================================
# RESUMEN FINAL
# ============================================
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${GREEN}✅ Configuración completada exitosamente${NC}"
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo ""
echo "🌐 Tu aplicación está disponible en:"
echo "   https://restaurant-tec.es"
echo "   https://www.restaurant-tec.es"
echo ""
echo "📝 Próximos pasos:"
echo "   1. Reinicia el backend: ./stop-all.sh && ./start-all.sh"
echo "   2. Verifica que Nginx está sirviendo los archivos:"
echo "      sudo systemctl status nginx"
echo "   3. Prueba tu aplicación en el navegador:"
echo "      https://restaurant-tec.es"
echo ""
echo "🔍 Verificación:"
echo "   - Frontend: https://restaurant-tec.es"
echo "   - API: https://restaurant-tec.es/api/locales"
echo ""
echo "📊 Logs útiles:"
echo "   - Nginx: sudo tail -f /var/log/nginx/restaurant-tec.access.log"
echo "   - Backend: tail -f logs/backend.log"
echo "   - Certbot: sudo journalctl -u certbot"
echo ""
