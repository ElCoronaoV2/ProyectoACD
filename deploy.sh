#!/bin/bash

# Script de despliegue para Restaurant-Tec
# Uso: ./deploy.sh [build|start|stop|restart|logs|status]

set -e

# Colores
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m'

PROJECT_DIR="/home/proyectoacd/ProyectoACD"
cd "$PROJECT_DIR"

show_help() {
    echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
    echo -e "${BLUE}  Restaurant-Tec - Script de Despliegue${NC}"
    echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
    echo ""
    echo "Uso: ./deploy.sh [comando]"
    echo ""
    echo "Comandos:"
    echo "  build     Compila frontend y backend (rebuilds images)"
    echo "  start     Inicia todos los servicios"
    echo "  stop      Para todos los servicios"
    echo "  restart   Reinicia todos los servicios"
    echo "  logs      Ver logs en tiempo real"
    echo "  status    Ver estado de los contenedores"
    echo "  full      Compilar e iniciar todo (build + start)"
    echo ""
}

do_build() {
    echo -e "${YELLOW}[1/2] Parando servicios existentes...${NC}"
    docker compose down 2>/dev/null || true
    
    echo -e "${YELLOW}[2/2] Compilando imágenes...${NC}"
    docker compose build --no-cache
    
    echo -e "${GREEN}✅ Compilación completada${NC}"
}

do_start() {
    echo -e "${YELLOW}Iniciando servicios...${NC}"
    docker compose up -d
    
    echo -e "${YELLOW}Esperando a que los servicios estén listos...${NC}"
    sleep 5
    
    do_status
    echo ""
    echo -e "${GREEN}✅ Servicios iniciados${NC}"
    echo -e "${BLUE}🌐 Accede a: https://restaurant-tec.es${NC}"
}

do_stop() {
    echo -e "${YELLOW}Parando servicios...${NC}"
    docker compose down
    echo -e "${GREEN}✅ Servicios parados${NC}"
}

do_restart() {
    do_stop
    do_start
}

do_logs() {
    echo -e "${YELLOW}Mostrando logs (Ctrl+C para salir)...${NC}"
    docker compose logs -f
}

do_status() {
    echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
    echo -e "${BLUE}  Estado de los Servicios${NC}"
    echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
    docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}" | grep -E "restaurant|NAMES"
}

do_full() {
    echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
    echo -e "${BLUE}  Despliegue Completo${NC}"
    echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
    echo ""
    do_build
    echo ""
    do_start
}

# Comando principal
case "${1:-help}" in
    build)   do_build ;;
    start)   do_start ;;
    stop)    do_stop ;;
    restart) do_restart ;;
    logs)    do_logs ;;
    status)  do_status ;;
    full)    do_full ;;
    *)       show_help ;;
esac
