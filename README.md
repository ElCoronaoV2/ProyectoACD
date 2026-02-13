# RestaurantTec 🍽️

Sistema de gestión de restaurantes y reservas online con inteligencia artificial para análisis de alérgenos.

## 🚀 Características

- **Gestión de Restaurantes**: CRUD completo con ubicación GPS y mapa interactivo
- **Sistema de Reservas**: Reservas online con integración de pagos Stripe
- **Menús Programables**: Gestión de menús con programación por fechas
- **Análisis de Alérgenos**: IA integrada (Ollama) para detectar alérgenos automáticamente
- **Multi-Roles**: Sistema de usuarios con 5 roles (USER, CEO, DIRECTOR, EMPLEADO, ADMIN)
- **Autenticación Segura**: JWT con verificación de email y recuperación de contraseña
- **Dashboard**: Estadísticas en tiempo real y gestión administrativa

## 📚 Documentación

Toda la documentación técnica está en la carpeta [`docs/`](docs/):

- [Modelo ER](docs/ER_MODEL.md) - Diagrama de base de datos
- [Diagrama UML](docs/UML_CLASS_DIAGRAM.md) - Clases del dominio
- [API Documentation](docs/API_DOCUMENTATION.md) - Referencia de endpoints
- [JavaDoc](docs/JAVADOC.md) - Documentación del código Java

## 🛠️ Tecnologías

### Backend
- **Java 21** + Spring Boot 3.5.9
- PostgreSQL 16
- JWT Authentication
- Stripe API
- Ollama (LLM local)

### Frontend
- **Angular 18.2**
- Tailwind CSS
- Google Maps API
- Stripe Elements

## 🔧 Instalación

### 1. Configurar variables de entorno

```bash
./setup-env.sh
```

### 2. Iniciar servicios con PM2

```bash
./start-all.sh
```

O desplegar todo:

```bash
./pm2-deploy.sh all
```

### 3. Detener servicios

```bash
./stop-all.sh
```

## 📁 Estructura del Proyecto

```
ProyectoACD/
├── tec/                    # Backend (Spring Boot)
│   ├── src/
│   │   ├── main/java/
│   │   │   └── com/restaurant/tec/
│   │   │       ├── controller/   # REST Controllers
│   │   │       ├── service/      # Business Logic
│   │   │       ├── entity/       # JPA Entities
│   │   │       ├── repository/   # Data Access
│   │   │       ├── security/     # JWT & Auth
│   │   │       └── dto/          # Data Transfer Objects
│   │   └── resources/
│   │       └── application.properties
│   └── seeds/              # SQL para testing
├── frontend/               # Frontend (Angular)
│   └── src/
│       ├── app/
│       │   ├── core/       # Guards, Interceptors, Services
│       │   ├── features/   # Componentes por feature
│       │   └── shared/     # Componentes compartidos
│       └── assets/
├── docs/                   # Documentación técnica
└── ecosystem.config.js     # Configuración PM2
```

## 🔐 Seguridad

- ✅ Rate Limiting en endpoints críticos
- ✅ Validación de roles estricta
- ✅ JWT con expiración de 24 horas
- ✅ Variables de entorno para secretos
- ✅ CORS configurado
- ✅ Verificación de email obligatoria

## 📝 Licencia

Proyecto académico - RestaurantTec Team 2026