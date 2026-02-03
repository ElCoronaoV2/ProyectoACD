#!/bin/bash
# restaurant_manager.sh - Script de gestión del proyecto

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m'

print_header() {
    echo -e "${CYAN}"
    echo "╔══════════════════════════════════════════╗"
    echo "║      🚀 Restaurant Project Manager       ║"
    echo "╚══════════════════════════════════════════╝"
    echo -e "${NC}"
}

print_status() {
    echo -e "${GREEN}✓${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}⚠${NC} $1"
}

print_error() {
    echo -e "${RED}✗${NC} $1"
}

# Esperar a que un servicio esté disponible
wait_for_service() {
    local url=$1
    local name=$2
    local max_attempts=30
    local attempt=1
    
    echo -e "${YELLOW}⏳ Esperando a que $name esté disponible...${NC}"
    
    while [ $attempt -le $max_attempts ]; do
        if curl -s -o /dev/null -w "%{http_code}" "$url" 2>/dev/null | grep -q "200\|304\|401\|403"; then
            print_status "$name está listo"
            return 0
        fi
        echo -n "."
        sleep 1
        attempt=$((attempt + 1))
    done
    
    print_warning "$name puede tardar un poco más"
    return 1
}

# check_database eliminado (gestionado por PM2)

check_pm2() {
    if ! command -v pm2 &> /dev/null; then
        echo -e "${YELLOW}PM2 no está instalado. Intentando instalarlo...${NC}"
        if command -v npm &> /dev/null; then
             # Try installing locally if global fails or just suggest it
             npm install pm2 -g || echo -e "${RED}No se pudo instalar pm2 globalmente. Intenta: npm install -g pm2${NC}"
        else
            print_error "npm no encontrado. Por favor instala Node.js y npm."
            return 1
        fi
    fi
}

show_menu() {
    print_header
    echo -e "${BLUE}Estado actual:${NC}"
    if command -v pm2 &> /dev/null; then
        pm2 status 2>/dev/null | head -10
    else
        echo "PM2 no detectado"
    fi
    echo ""
    echo -e "${CYAN}═══════════════════════════════════════════${NC}"
    echo ""
    echo -e "  ${GREEN}1${NC}) 🚀 Iniciar todo"
    echo -e "  ${GREEN}2${NC}) ⏹  Detener todo"
    echo -e "  ${GREEN}3${NC}) 🔄 Reiniciar todo"
    echo -e "  ${GREEN}4${NC}) 📊 Ver estado"
    echo ""
    echo -e "  ${BLUE}5${NC}) 🔨 Build frontend"
    echo -e "  ${BLUE}6${NC}) 🔨 Build backend + reiniciar"
    echo -e "  ${BLUE}7${NC}) 🚀 Deploy FULL (build + deploy)"
    echo ""
    echo -e "  ${YELLOW}8${NC}) 📋 Ver logs (todos)"
    echo -e "  ${YELLOW}9${NC}) 📋 Ver logs backend"
    echo ""
    echo -e "  ${RED}0${NC}) ❌ Salir"
    echo ""
    echo -e "${CYAN}═══════════════════════════════════════════${NC}"
    echo ""
}

do_start() {
    echo -e "\n${BLUE}🚀 Iniciando todos los servicios...${NC}\n"

    cd "$SCRIPT_DIR"
    
    # Detener primero si hay algo corriendo
    pm2 delete all 2>/dev/null || true
    
    # Iniciar desde ecosystem.config.js
    pm2 start ecosystem.config.js
    
    # Esperar a que el backend esté listo
    wait_for_service "http://localhost:8080/api/locales" "Backend"
    
    pm2 save
    
    echo ""
    pm2 status
    echo -e "\n${GREEN}✅ Servicios iniciados${NC}"
    echo -e "  🌐 Frontend: http://restaurant-tec.es (via Nginx)"
    echo -e "  🔌 Backend:  http://localhost:8080"
}

do_stop() {
    echo -e "\n${YELLOW}⏹  Deteniendo todos los servicios...${NC}\n"
    pm2 stop all 2>/dev/null
    print_status "Servicios detenidos"
}

do_restart() {
    echo -e "\n${BLUE}🔄 Reiniciando todos los servicios...${NC}\n"
    
    pm2 restart ecosystem.config.js
    
    wait_for_service "http://localhost:8080/api/locales" "Backend"
    
    pm2 save
    pm2 status
    print_status "Servicios reiniciados"
}

do_status() {
    echo -e "\n${BLUE}📊 Estado de los servicios:${NC}\n"
    pm2 status
    echo -e "\n${BLUE}🔌 Puertos:${NC}"
    ss -tuln 2>/dev/null | grep -E "3000|8080" || true
}

do_build_frontend() {
    echo -e "\n${BLUE}🔨 Reconstruyendo frontend...${NC}\n"
    cd "$SCRIPT_DIR/frontend"
    npm install
    npm run build -- --configuration production
    
    echo -e "\n${GREEN}✓${NC} Frontend compilado"
    echo -e "  📁 Archivos en: dist/frontend/browser"
    echo -e "  🌐 Nginx sirve desde esta ubicación"
    
    # Recargar Nginx para asegurar que sirve los nuevos archivos
    sudo systemctl reload nginx 2>/dev/null || true
    print_status "Frontend reconstruido y listo"
}

do_build_backend() {
    echo -e "\n${BLUE}🔨 Reconstruyendo backend...${NC}\n"
    cd "$SCRIPT_DIR/tec"
    ./mvnw clean package -DskipTests
    
    echo -e "\n${BLUE}🔄 Reiniciando backend...${NC}"
    pm2 restart restaurant-backend 2>/dev/null || true
    
    wait_for_service "http://localhost:8080/api/locales" "Backend"
    print_status "Backend reconstruido y listo"
}

do_build_all() {
    echo -e "\n${BLUE}🚀 DEPLOY FULL - Ejecutando pm2-deploy.sh...${NC}\n"
    cd "$SCRIPT_DIR"
    ./pm2-deploy.sh all
}

do_logs_all() {
    pm2 logs --lines 30
}

do_logs_backend() {
    pm2 logs restaurant-backend --lines 50
}

wait_key() {
    echo ""
    read -p "Presiona ENTER para continuar..." key
}

# Main
check_pm2

while true; do
    clear
    show_menu
    read -p "Selecciona una opción [0-10]: " choice
    
    case $choice in
        1) do_start; wait_key ;;
        2) do_stop; wait_key ;;
        3) do_restart; wait_key ;;
        4) do_status; wait_key ;;
        5) do_build_frontend; wait_key ;;
        6) do_build_backend; wait_key ;;
        7) do_build_all; wait_key ;;
        8) do_logs_all ;;
        9) do_logs_backend ;;
        0|q|Q) 
            echo -e "\n${GREEN}👋 ¡Hasta luego!${NC}\n"
            exit 0 
            ;;
        *)
            print_error "Opción no válida"
            sleep 1
            ;;
    esac
done
