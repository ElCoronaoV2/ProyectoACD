#!/bin/bash

# Script para iniciar todo el sistema Restaurant-tec
# Autor: Antigravity
# Fecha: 2026-01-27

echo "🚀 Iniciando Restaurant-tec..."
echo ""

# ============================================
# Cargar variables de entorno desde .env
# ============================================
PROJECT_DIR="/home/proyectoacd/ProyectoACD"
if [ -f "$PROJECT_DIR/.env" ]; then
    set -a
    source "$PROJECT_DIR/.env"
    set +a
    echo "✓ Variables de entorno cargadas desde .env"
else
    echo "⚠️  Advertencia: archivo .env no encontrado"
    echo "   Algunos servicios pueden no estar configurados correctamente"
fi
echo ""

# Colores para output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Función para verificar si un puerto está en uso
check_port() {
    if lsof -Pi :$1 -sTCP:LISTEN -t >/dev/null 2>&1 ; then
        return 0
    else
        return 1
    fi
}

# 1. Verificar e iniciar PostgreSQL
echo -e "${YELLOW}[1/3] Verificando PostgreSQL...${NC}"
if docker ps | grep -q restaurant-db; then
    echo -e "${GREEN}✅ PostgreSQL ya está corriendo${NC}"
else
    echo "Iniciando PostgreSQL..."
    docker start restaurant-db 2>/dev/null || {
        echo -e "${RED}❌ Error: No se pudo iniciar PostgreSQL${NC}"
        echo "Ejecuta: docker ps -a | grep postgres"
        exit 1
    }
    sleep 3
    echo -e "${GREEN}✅ PostgreSQL iniciado${NC}"
fi
echo ""

# 2. Iniciar Backend (Spring Boot)
echo -e "${YELLOW}[2/3] Iniciando Backend (Spring Boot)...${NC}"
if check_port 8080; then
    echo -e "${RED}⚠️  Puerto 8080 ya está en uso${NC}"
    read -p "¿Deseas detener el proceso existente? (s/n): " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Ss]$ ]]; then
        lsof -ti:8080 | xargs kill -9 2>/dev/null
        sleep 2
    else
        echo "Saltando inicio del backend..."
    fi
fi

if ! check_port 8080; then
    cd tec
    echo "Compilando y arrancando Spring Boot..."
    nohup ./mvnw spring-boot:run > ../logs/backend.log 2>&1 &
    BACKEND_PID=$!
    echo "Backend iniciado con PID: $BACKEND_PID"
    
    # Esperar a que el backend esté listo
    echo -n "Esperando a que el backend esté listo"
    for i in {1..30}; do
        if check_port 8080; then
            echo ""
            echo -e "${GREEN}✅ Backend corriendo en http://localhost:8080${NC}"
            break
        fi
        echo -n "."
        sleep 1
    done
    
    if ! check_port 8080; then
        echo ""
        echo -e "${RED}❌ El backend no arrancó correctamente${NC}"
        echo "Revisa los logs en: logs/backend.log"
        exit 1
    fi
    cd ..
fi
echo ""

# 3. Iniciar Frontend (Angular)
echo -e "${YELLOW}[3/3] Iniciando Frontend (Angular)...${NC}"
if check_port 4200; then
    echo -e "${RED}⚠️  Puerto 4200 ya está en uso${NC}"
    read -p "¿Deseas detener el proceso existente? (s/n): " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Ss]$ ]]; then
        lsof -ti:4200 | xargs kill -9 2>/dev/null
        sleep 2
    else
        echo "Saltando inicio del frontend..."
    fi
fi

if ! check_port 4200; then
    cd frontend
    echo "Iniciando servidor de desarrollo Angular..."
    nohup npm start > ../logs/frontend.log 2>&1 &
    FRONTEND_PID=$!
    echo "Frontend iniciado con PID: $FRONTEND_PID"
    
    # Esperar a que el frontend esté listo
    echo -n "Esperando a que el frontend esté listo"
    for i in {1..60}; do
        if check_port 4200; then
            echo ""
            echo -e "${GREEN}✅ Frontend corriendo en http://localhost:4200${NC}"
            break
        fi
        echo -n "."
        sleep 1
    done
    
    if ! check_port 4200; then
        echo ""
        echo -e "${RED}❌ El frontend no arrancó correctamente${NC}"
        echo "Revisa los logs en: logs/frontend.log"
        exit 1
    fi
    cd ..
fi
echo ""

# Resumen
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${GREEN}✅ Sistema Restaurant-tec iniciado correctamente${NC}"
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo ""
echo "📊 Servicios disponibles:"
echo "  🗄️  PostgreSQL:  localhost:5432"
echo "  🔧 Backend API:  http://localhost:8080"
echo "  🎨 Frontend:     http://localhost:4200"
echo ""
echo "📝 Logs:"
echo "  Backend:  logs/backend.log"
echo "  Frontend: logs/frontend.log"
echo ""
echo "🛑 Para detener todos los servicios ejecuta: ./stop-all.sh"
echo ""
