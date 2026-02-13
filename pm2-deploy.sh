#!/bin/bash
# pm2-deploy.sh - Script de despliegue automático con PM2
# Uso: ./pm2-deploy.sh [all|frontend|backend]

set -e

PROJECT_DIR="/home/proyectoacd/ProyectoACD"
FRONTEND_DIR="$PROJECT_DIR/frontend"
BACKEND_DIR="$PROJECT_DIR/tec"

# ============================================
# Cargar variables de entorno desde .env
# ============================================
if [ -f "$PROJECT_DIR/.env" ]; then
    set -a
    source "$PROJECT_DIR/.env"
    set +a
    echo "✓ Variables de entorno cargadas desde .env"
else
    echo "⚠️  Advertencia: archivo .env no encontrado en $PROJECT_DIR"
    echo "   El despliegue puede fallar si falta configuración"
fi

# Colores
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m'

cd "$PROJECT_DIR"

log_info() { echo -e "${BLUE}ℹ${NC} $1"; }
log_success() { echo -e "${GREEN}✓${NC} $1"; }
log_warning() { echo -e "${YELLOW}⚠${NC} $1"; }
log_error() { echo -e "${RED}✗${NC} $1"; }

print_header() {
    echo -e "${BLUE}"
    echo "╔══════════════════════════════════════════╗"
    echo "║    🚀 PM2 Deploy - restaurant.tec.es     ║"
    echo "╚══════════════════════════════════════════╝"
    echo -e "${NC}"
}

build_frontend() {
    log_info "Compilando frontend Angular..."
    cd "$FRONTEND_DIR"
    
    npm install --silent 2>/dev/null
    npm run build -- --configuration production
    
    log_success "Frontend compilado en dist/frontend/browser"
    log_info "Nginx ya sirve desde esta ubicación"
}

build_backend() {
    log_info "Compilando backend Spring Boot..."
    cd "$BACKEND_DIR"
    
    ./mvnw clean package -DskipTests -q
    
    log_success "Backend compilado: target/tec-0.0.1-SNAPSHOT.jar"
}

restart_services() {
    cd "$PROJECT_DIR"
    
    log_info "Reiniciando servicios PM2 (Hard Reset)..."
    
    # Detener y eliminar todos los procesos para asegurar un inicio limpio
    pm2 delete all 2>/dev/null || true
    
    # Esperar un momento
    sleep 2
    
    # Iniciar todo desde el ecosistema
    pm2 start ecosystem.config.js --update-env
    
    # Esperar a que el backend esté completamente listo (más tiempo)
    log_info "Esperando a que el backend esté listo (esto puede tardar ~30s)..."
    
    local max_attempts=60
    local attempt=1
    local ready=false
    
    while [ $attempt -le $max_attempts ]; do
        # Verificar que responde correctamente
        local status=$(curl -s -o /dev/null -w "%{http_code}" "http://localhost:8080/api/locales" 2>/dev/null)
        
        if [ "$status" = "200" ]; then
            ready=true
            break
        fi
        
        if [ $((attempt % 5)) -eq 0 ]; then
             echo -n "."
        fi
        sleep 1
        attempt=$((attempt + 1))
    done
    
    echo ""
    
    if [ "$ready" = true ]; then
        log_success "Backend listo (HTTP 200)"
        
        # Verificación adicional con origen externo
        local cors_check=$(curl -s -o /dev/null -w "%{http_code}" -H "Origin: https://www.restaurant-tec.es" "http://localhost:8080/api/locales" 2>/dev/null)
        if [ "$cors_check" = "200" ]; then
            log_success "CORS verificado OK"
        else
            log_warning "CORS puede tener problemas (HTTP $cors_check)"
        fi
    else
        log_error "Backend no respondió después de ${max_attempts}s"
        log_info "Revisando logs..."
        pm2 logs restaurant-backend --lines 10 --nostream
        return 1
    fi
    
    pm2 save
}

reload_nginx() {
    log_info "Recargando Nginx..."
    sudo systemctl reload nginx 2>/dev/null || sudo nginx -s reload 2>/dev/null || log_warning "No se pudo recargar Nginx"
    log_success "Nginx recargado"
}

deploy_all() {
    print_header
    
    echo -e "\n${YELLOW}[1/4] Build Backend${NC}"
    build_backend
    
    echo -e "\n${YELLOW}[2/4] Build Frontend${NC}"
    build_frontend
    
    echo -e "\n${YELLOW}[3/4] Reiniciar Servicios${NC}"
    restart_services
    
    echo -e "\n${YELLOW}[4/4] Recargar Nginx${NC}"
    reload_nginx
    
    echo ""
    echo -e "${GREEN}════════════════════════════════════════════${NC}"
    echo -e "${GREEN}  ✅ Despliegue completado${NC}"
    echo -e "${GREEN}════════════════════════════════════════════${NC}"
    echo ""
    echo -e "  🌐 Frontend: http://restaurant-tec.es"
    echo -e "  🔌 Backend:  http://localhost:8080"
    echo ""
    pm2 status
}

deploy_frontend_only() {
    print_header
    build_frontend
    reload_nginx
    log_success "Frontend desplegado"
}

deploy_backend_only() {
    print_header
    build_backend
    restart_services
    log_success "Backend desplegado"
}

show_usage() {
    echo "Uso: ./pm2-deploy.sh [comando]"
    echo ""
    echo "Comandos:"
    echo "  all       Compilar y desplegar todo (por defecto)"
    echo "  frontend  Solo compilar y desplegar frontend"
    echo "  backend   Solo compilar y desplegar backend"
    echo "  status    Ver estado de servicios"
    echo ""
    echo "Notas:"
    echo "  • Variables de entorno se cargan automáticamente desde .env"
    echo "  • Asegúrate de que .env existe en $PROJECT_DIR"
    echo "  • Puedes crear .env con: cp .env.example .env"
    echo ""
}

# Main
case "${1:-all}" in
    all)      deploy_all ;;
    frontend) deploy_frontend_only ;;
    backend)  deploy_backend_only ;;
    status)   pm2 status ;;
    *)        show_usage ;;
esac
