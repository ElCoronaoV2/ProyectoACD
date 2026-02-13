# Documentación de la API

## Base URL
- Producción: `https://restaurant-tec.es`
- Desarrollo: `http://localhost:8080`

## Autenticación
- JWT Bearer Token: `Authorization: Bearer <token>`
- Endpoints públicos: ver sección de Endpoints.

## Códigos de estado comunes
- 200 OK
- 201 Created
- 400 Bad Request
- 401 Unauthorized
- 403 Forbidden
- 404 Not Found

## Endpoints

### Autenticación
- POST `/api/auth/login` → Login, retorna JWT y datos de usuario.
- POST `/api/auth/register` → Registro (envía verificación por email).
- GET `/api/auth/verify?token=...` → Verificación de cuenta.
- POST `/api/auth/forgot-password?email=...` → Solicita reset.
- POST `/api/auth/reset-password?token=...` → Resetea contraseña.

### Usuarios
- GET `/api/users/profile` → Perfil del usuario autenticado.
- PUT `/api/users/profile` → Actualiza perfil (nombre, teléfono, alérgenos).

### Locales (público)
- GET `/api/locales` → Lista de locales.
- GET `/api/locales/{id}` → Detalle de local.
- GET `/api/locales/{id}/menus` → Menú del día (lista con 0/1 ítems).
- GET `/api/locales/{id}/menu-del-dia` → Menú del día (objeto o mensaje).
- POST `/api/locales/{id}/reviews` → Añadir valoración al local.

### Locales (admin)
- POST `/api/admin/locales` → Crear local.
- PUT `/api/admin/locales/{id}` → Actualizar local.
- DELETE `/api/admin/locales/{id}` → Eliminar local.

### Menús (admin)
- POST `/api/admin/menus` → Crear menú.
- PUT `/api/admin/menus/{id}` → Actualizar menú.
- DELETE `/api/admin/menus/{id}` → Eliminar menú.
- GET `/api/admin/menus/{id}` → Obtener menú.

### Programación de menús (admin)
- POST `/api/admin/locales/{localId}/schedule` → Programar menú por fecha.
- GET `/api/admin/locales/{localId}/schedule?year=YYYY&month=MM` → Obtener programación.
- DELETE `/api/admin/locales/{localId}/schedule/{fecha}` → Eliminar programación.

### Menús (público autenticado)
- POST `/api/menus/{id}/resenas` → Añadir reseña a menú.

### Reservas
- POST `/api/reservas/create-payment-intent` → Crear intento de pago (Stripe).
- POST `/api/reservas` → Crear reserva (usuario autenticado).
- POST `/api/reservas/guest` → Crear reserva de invitado.
- GET `/api/reservas/mis-reservas` → Reservas del usuario autenticado.
- GET `/api/reservas/local/{localId}` → Reservas por local (filtro opcional por fecha).
- GET `/api/reservas/availability?localId=...&fechaHora=...` → Disponibilidad.
- GET `/api/reservas/upcoming?minutes=...` → Reservas confirmadas próximas (n8n).
- GET `/api/reservas/unconfirmed-urgent?minutes=...` → Reservas pendientes urgentes (n8n).
- PUT `/api/reservas/{id}/estado` → Actualiza estado de reserva.
- POST `/api/reservas/{id}/confirm` → Confirmar asistencia.

### Gestión (CEO/Director/Empleado)
- GET `/api/management/restaurantes` → Restaurantes del CEO o todos si Director.
- POST `/api/management/restaurantes` → Crear restaurante.
- PUT `/api/management/restaurantes/{id}` → Actualizar restaurante.
- DELETE `/api/management/restaurantes/{id}` → Eliminar restaurante.
- GET `/api/management/restaurantes/{id}` → Ver restaurante.
- POST `/api/management/restaurantes/{localId}/empleados` → Crear empleado.
- GET `/api/management/restaurantes/{localId}/empleados` → Listar empleados.
- DELETE `/api/management/empleados/{id}` → Eliminar empleado.
- PUT `/api/management/empleados/{id}` → Actualizar empleado.
- POST `/api/management/restaurantes/{localId}/menus` → Crear menú en local.
- GET `/api/management/restaurantes/{localId}/menus` → Listar menús del local.
- DELETE `/api/management/menus/{id}` → Borrar menú (o mover a general).
- PUT `/api/management/menus/{id}` → Actualizar menú (pendiente de implementación completa).
- GET `/api/management/menus/general` → Biblioteca de menús.
- PUT `/api/management/menus/{menuId}/assign/{localId}` → Importar menú a local.

### Staff
- GET `/api/staff/my-restaurant` → Restaurante asignado al empleado.

### Admin usuarios
- GET `/api/admin/ceos` → Listar CEOs.
- GET `/api/admin/users?role=...` → Listar usuarios (filtro opcional).
- GET `/api/admin/stats` → Estadísticas.
- POST `/api/admin/users` → Crear usuario.
- PUT `/api/admin/users/{id}` → Actualizar usuario.
- DELETE `/api/admin/users/{id}` → Eliminar usuario.

### IA (admin)
- POST `/api/admin/ai/analyze` → Analizar alérgenos.

### Info
- GET `/api` → Estado API y endpoints principales.

## Notas
- Algunos endpoints requieren JWT según configuración de seguridad.
- Verificar roles con el equipo según reglas actuales de autorización.
