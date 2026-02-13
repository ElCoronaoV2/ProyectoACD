# 1. PRUEBAS - Hito 5 Fase 1

**Estado General**: En Progreso (5% - Integración completada)

## ✅ Fase 0: Integración de Componentes
- [x] Consolidación de código (Frontend, Backend, Servicios)
- [x] Verificación de compatibilidad de dependencias
- [x] Validación de variables de entorno
- [x] Pruebas de conexión Frontend-Backend
- [x] Informe detallado: [INTEGRACION_INFORME.md](INTEGRACION_INFORME.md)

**RESULTADO**: ✅ **INTEGRACIÓN EXITOSA - Listo para Testing**

## Pruebas Funcionales

### Testing Unitario
- [ ] Controllers (MockMvc)
  - [ ] AuthController - casos de login, registro, verificación
  - [ ] AdminController - CRUD de usuarios, estadísticas
  - [ ] ReservaController - creación, cancelación, listado
  - [ ] LocalController - CRUD de restaurantes
  - [ ] MenuController - CRUD de menús

- [ ] Services
  - [ ] UserService - registro, verificación, reseteo
  - [ ] ReservaService - disponibilidad, creación
  - [ ] EmailService - envío de correos
  - [ ] AiService - análisis de alérgenos

- [ ] Repositories
  - [ ] Queries personalizadas
  - [ ] Métodos de búsqueda

### Testing Frontend
- [ ] Authentication
  - [ ] LoginComponent - login exitoso/fallido
  - [ ] RegisterComponent - validaciones
  - [ ] Password reset flow

- [ ] Features
  - [ ] ReservaComponent - creación de reserva
  - [ ] MenuComponent - visualización de menús
  - [ ] ProfileComponent - actualización de perfil

- [ ] Servicios
  - [ ] AuthService - token handling
  - [ ] ReservaService - API calls
  - [ ] NotificationService - toast messages

## Pruebas de Integración

### Frontend-Backend
- [ ] AuthFlow: Registro → Verificación → Login → Token
- [ ] ReservaFlow: Listar locales → Crear reserva → Pagar → Confirmación
- [ ] MenuFlow: Listar restaurantes → Ver menús → Dejar resena
- [ ] AdminFlow: Login admin → Estadísticas → Gestión usuarios

### Base de Datos
- [ ] Transacciones (crear reserva + pago)
- [ ] Integridad referencial
- [ ] Índices y performance

### Servicios Externos
- [ ] Stripe - Crear PaymentIntent, confirmar pago
- [ ] Gmail - Envío de emails
- [ ] Ollama - Análisis de alérgenos
- [ ] Google Maps - Carga de mapa

## Pruebas de Rendimiento

### Load Testing
- [ ] 100 usuarios concurrentes
- [ ] 1000 requests/min
- [ ] Tiempo de respuesta < 500ms

### Stress Testing
- [ ] Límite de conexiones
- [ ] Degradación gradual
- [ ] Recovery tras fallo

### Memory Profiling
- [ ] Memory leaks
- [ ] Garbage collection
- [ ] CPU usage

## Pruebas de Seguridad

### Seguridad Backend
- [ ] CSRF tokens
- [ ] SQL injection
- [ ] XXE (XML injection)
- [ ] Autenticación obligatoria en endpoints seguros

### Seguridad Frontend
- [ ] XSS prevention
- [ ] Validaciones client-side
- [ ] Almacenamiento seguro de tokens

### Credenciales
- [ ] Variables de entorno no en logs
- [ ] Secretos en .env (no en Git)

## Plan de Ejecución

```
Semana 1: Pruebas Unitarias (Back + Front)
Semana 2: Pruebas de Integración
Semana 3: Pruebas de Rendimiento y Seguridad
```

## Herramientas Necesarias
- JUnit 5 (Backend)
- Mockito (Backend)
- Jasmine/Karma (Frontend)
- JMeter (Load testing)
- OWASP ZAP (Security testing)

---

**ESTADO**: Requiere planificación y ejecución
**ESTIMACIÓN**: 2-3 semanas de trabajo
**RESPONSABLE**: Equipo QA/Testing
