#!/bin/bash

# Script de configuración automática para restaurant-tec.es
# Autor: Antigravity
# Fecha: 2026-01-27

set -e  # Salir si hay algún error

# Colores
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m'

echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${BLUE}  Configuración de restaurant-tec.es${NC}"
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo ""

# Verificar que se ejecuta como root
if [ "$EUID" -ne 0 ]; then 
    echo -e "${RED}❌ Este script debe ejecutarse como root (sudo)${NC}"
    exit 1
fi

DOMAIN="restaurant-tec.es"
IP_PUBLIC="37.14.218.204"
PROJECT_DIR="/home/proyectoacd/ProyectoACD"
FRONTEND_DIR="$PROJECT_DIR/frontend"
DIST_DIR="$FRONTEND_DIR/dist/frontend/browser"

# Paso 1: Instalar Nginx
echo -e "${YELLOW}[1/7] Instalando Nginx...${NC}"
apt update
apt install nginx -y
systemctl enable nginx
systemctl start nginx
echo -e "${GREEN}✅ Nginx instalado${NC}"
echo ""

# Paso 2: Configurar Firewall
echo -e "${YELLOW}[2/7] Configurando firewall...${NC}"
ufw allow 80/tcp
ufw allow 443/tcp
ufw allow 22/tcp
echo -e "${GREEN}✅ Firewall configurado${NC}"
echo ""

# Paso 3: Compilar Frontend
echo -e "${YELLOW}[3/7] Compilando frontend para producción...${NC}"
cd "$FRONTEND_DIR"
sudo -u proyectoacd npm run build
echo -e "${GREEN}✅ Frontend compilado${NC}"
echo ""

# Paso 4: Crear configuración de Nginx
echo -e "${YELLOW}[4/7] Configurando Nginx...${NC}"
cat > /etc/nginx/sites-available/$DOMAIN << 'EOF'
server {
    listen 80 default_server;
    listen [::]:80 default_server;
    
    server_name restaurant-tec.es www.restaurant-tec.es;
    
    access_log /var/log/nginx/restaurant-tec.access.log;
    error_log /var/log/nginx/restaurant-tec.error.log;
    
    root /home/proyectoacd/ProyectoACD/frontend/dist/frontend/browser;
    index index.html;
    
    location / {
        try_files $uri $uri/ /index.html;
    }
    
    location /api/ {
        proxy_pass http://localhost:8080;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_cache_bypass $http_upgrade;
    }
    
    location ~* \.(jpg|jpeg|png|gif|ico|css|js|svg|woff|woff2|ttf|eot)$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }
}

server {
    listen 80;
    listen [::]:80;
    
    server_name api.restaurant-tec.es;
    
    location / {
        proxy_pass http://localhost:8080;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_cache_bypass $http_upgrade;
    }
}
EOF

# Activar configuración
ln -sf /etc/nginx/sites-available/$DOMAIN /etc/nginx/sites-enabled/
rm -f /etc/nginx/sites-enabled/default

# Verificar configuración
nginx -t

# Recargar Nginx
systemctl reload nginx
echo -e "${GREEN}✅ Nginx configurado${NC}"
echo ""

# Paso 5: Instalar Certbot
echo -e "${YELLOW}[5/7] Instalando Certbot para SSL...${NC}"
apt install certbot python3-certbot-nginx -y
echo -e "${GREEN}✅ Certbot instalado${NC}"
echo ""

# Paso 6: Información sobre DNS
echo -e "${YELLOW}[6/7] Configuración DNS${NC}"
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo ""
echo "Antes de continuar, configura estos registros DNS en tu proveedor:"
echo ""
echo "Tipo    Nombre    Valor"
echo "A       @         $IP_PUBLIC"
echo "A       www       $IP_PUBLIC"
echo "A       api       $IP_PUBLIC"
echo ""
echo -e "${YELLOW}Espera a que los DNS se propaguen (puede tardar hasta 48h)${NC}"
echo ""
read -p "¿Has configurado los DNS y están propagados? (s/n): " -n 1 -r
echo
if [[ ! $REPLY =~ ^[Ss]$ ]]; then
    echo -e "${YELLOW}⚠️  Configuración pausada. Ejecuta este script de nuevo cuando los DNS estén listos.${NC}"
    exit 0
fi
echo ""

# Paso 7: Obtener certificado SSL
echo -e "${YELLOW}[7/7] Obteniendo certificado SSL...${NC}"
echo ""
read -p "Introduce tu email para Let's Encrypt: " EMAIL

certbot --nginx \
    -d $DOMAIN \
    -d www.$DOMAIN \
    -d api.$DOMAIN \
    --non-interactive \
    --agree-tos \
    --email "$EMAIL" \
    --redirect

echo -e "${GREEN}✅ Certificado SSL configurado${NC}"
echo ""

# Resumen final
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${GREEN}✅ Configuración completada exitosamente${NC}"
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo ""
echo "🌐 Tu aplicación está disponible en:"
echo "   https://$DOMAIN"
echo "   https://www.$DOMAIN"
echo "   https://api.$DOMAIN"
echo ""
echo "📝 Logs de Nginx:"
echo "   sudo tail -f /var/log/nginx/restaurant-tec.access.log"
echo "   sudo tail -f /var/log/nginx/restaurant-tec.error.log"
echo ""
echo "🔄 Renovación automática de SSL configurada"
echo ""
echo "⚠️  IMPORTANTE:"
echo "   - Asegúrate de que los puertos 80 y 443 estén abiertos en tu router"
echo "   - Configura Port Forwarding si estás detrás de un router"
echo "   - Actualiza las variables de entorno del frontend si es necesario"
echo ""
