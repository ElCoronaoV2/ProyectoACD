#!/bin/bash

# Script para detener todo el sistema Restaurant-tec
# Autor: Antigravity
# Fecha: 2026-01-27

echo "🛑 Deteniendo Restaurant-tec..."
echo ""

# Colores
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

# 1. Detener Frontend (Angular)
echo -e "${YELLOW}[1/3] Deteniendo Frontend...${NC}"
pkill -f "npm start" 2>/dev/null
lsof -ti:4200 | xargs kill -9 2>/dev/null
sleep 2
echo -e "${GREEN}✅ Frontend detenido${NC}"
echo ""

# 2. Detener Backend (Spring Boot)
echo -e "${YELLOW}[2/3] Deteniendo Backend...${NC}"
pkill -f "mvnw spring-boot:run" 2>/dev/null
lsof -ti:8080 | xargs kill -9 2>/dev/null
sleep 2
echo -e "${GREEN}✅ Backend detenido${NC}"
echo ""

# 3. Detener PostgreSQL (opcional)
echo -e "${YELLOW}[3/3] PostgreSQL...${NC}"
read -p "¿Deseas detener PostgreSQL también? (s/n): " -n 1 -r
echo
if [[ $REPLY =~ ^[Ss]$ ]]; then
    docker stop restaurant-db
    echo -e "${GREEN}✅ PostgreSQL detenido${NC}"
else
    echo -e "${GREEN}✅ PostgreSQL sigue corriendo${NC}"
fi
echo ""

echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${GREEN}✅ Servicios detenidos correctamente${NC}"
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo ""
