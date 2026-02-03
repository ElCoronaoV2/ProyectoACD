#!/bin/bash
echo "Verifying deployment..."

echo "Checking containers..."
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"

# echo -e "\nChecking Backend Health (Direct)..."
# curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/api/health || echo "Failed to connect to backend directly"

echo -e "\nChecking Frontend via Domain (Host Header)..."
curl -s -H "Host: restaurant-tec.es" -o /dev/null -w "%{http_code}" http://localhost/ || echo "Failed to connect to frontend via domain"

echo -e "\nChecking Backend Proxy via Domain..."
curl -s -H "Host: restaurant-tec.es" -o /dev/null -w "%{http_code}" http://localhost/api/health || echo "Failed to connect to API via domain"
