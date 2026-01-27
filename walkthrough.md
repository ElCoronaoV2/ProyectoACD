# Configuración del Servidor restaurant-tec.es

Se han completado los pasos de configuración del servidor para desplegar la aplicación `restaurant-tec.es`.

## Estado Actual

| Componente | Estado | Detalle |
| :--- | :--- | :--- |
| **Nginx** | ✅ Activo | Configurado como Proxy Reverso y sirviendo Frontend. |
| **Firewall (UFW)** | ✅ Activo | Puertos 80, 443, 22 permitidos. |
| **Frontend** | ✅ Compilado | Build de producción en `dist/frontend/browser`. |
| **Backend** | ✅ Iniciado | Spring Boot corriendo en puerto 8080. |
| **Base de Datos** | ✅ Iniciado | PostgreSQL corriendo. |
| **DNS** | ⏳ Pendiente | Los DNS actuales no apuntan a la IP del servidor todavía. |
| **SSL/HTTPS** | ⏳ Pendiente | Requiere propagación de DNS para generar certificados. |

## Acciones Realizadas

1. **Configuración de Código**:
   - Verificado `CorsConfig.java` para permitir orígenes `https://restaurant-tec.es`.
   - Verificado `environment.prod.ts` apuntando a la API correcta.

2. **Instalación de Sistema**:
   - Ejecutado script de configuración.
   - Instalado Nginx y configurado `sites-available/restaurant-tec.es`.
   - Permisos de lectura corregidos para Nginx sobre el directorio de usuario.

3. **Verificación**:
   - `curl http://localhost` (simulando dominio) devuelve **200 OK**.
   - Backend iniciado correctamente.

## Pasos Pendientes (Para el Usuario)

> [!IMPORTANT]
> **Propagación DNS**: Debes esperar a que los cambios DNS en tu proveedor de dominio se propaguen.
> Verifica tu dominio con: `nslookup restaurant-tec.es` (Debe devolver tu IP: `37.14.218.204`).

Una vez los DNS apunten correctamente, ejecuta el siguiente comando para activar HTTPS:

```bash
sudo certbot --nginx -d restaurant-tec.es -d www.restaurant-tec.es -d api.restaurant-tec.es
```

O vuelve a ejecutar el script de configuración y responde "s" cuando pregunte por los DNS.
