#!/bin/bash

# Script de Setup Rápido - Variables de Entorno
# Ejecutar SOLO UNA VEZ después de hacer git pull

set -e

echo "🚀 Setup de Variables de Entorno - Restaurant-tec"
echo "=================================================="
echo ""

# Verificar que estamos en la carpeta correcta
if [ ! -f "docker-compose.yml" ]; then
    echo "❌ Error: Ejecuta este script desde /home/proyectoacd/ProyectoACD"
    exit 1
fi

# Step 1: Crear .env
if [ -f ".env" ]; then
    echo "⚠️  .env ya existe. ¿Deseas sobrescribirlo? (s/n)"
    read -n 1 -r
    if [[ ! $REPLY =~ ^[Ss]$ ]]; then
        echo "Saltando creación de .env"
    else
        cp .env.example .env
        echo "✓ .env creado desde .env.example"
    fi
else
    cp .env.example .env
    echo "✓ .env creado desde .env.example"
fi

echo ""
echo "📋 VARIABLES REQUERIDAS:"
echo ""
echo "1. 🔑 JWT_SECRET (REQUERIDO)"
echo "   Generar con: openssl rand -base64 32"
echo ""
echo "2. 📧 MAIL_USERNAME y MAIL_PASSWORD"
echo "   Gmail: https://myaccount.google.com/apppasswords"
echo ""
echo "3. 💳 STRIPE_PUBLIC_KEY y STRIPE_SECRET_KEY"
echo "   Obtener en: https://dashboard.stripe.com/apikeys"
echo ""
echo "4. 🤖 OLLAMA_URL (opcional)"
echo "   Por defecto: http://192.168.1.83:11434/api/generate"
echo ""

# Step 2: Preguntar si editar
echo "¿Deseas editar .env ahora? (s/n)"
read -n 1 -r
echo
if [[ $REPLY =~ ^[Ss]$ ]]; then
    # Detectar editor disponible
    if command -v nano &> /dev/null; then
        nano .env
    elif command -v vi &> /dev/null; then
        vi .env
    else
        echo "❌ No se encontró editor (nano/vi)"
        exit 1
    fi
fi

echo ""
echo "=================================================="
echo "✓ Setup completado"
echo ""
echo "Próximos pasos:"
echo "1. Verificar que .env está correcto: cat .env"
echo "2. Ejecutar tests: ./verify-security.sh"
echo "3. Iniciar contenedores: docker compose up -d"
echo "4. Ver logs: docker compose logs -f backend"
echo ""
