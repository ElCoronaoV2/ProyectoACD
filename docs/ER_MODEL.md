# Modelo Entidad-Relación (ER)

## Visión general
Este modelo describe las entidades principales del dominio y sus relaciones. Incluye claves primarias (PK), claves foráneas (FK) y cardinalidades clave para comprender dependencias de datos y soportar mantenimiento.

## Diagrama ER (Mermaid)
```mermaid
erDiagram
    USUARIOS {
        LONG id PK
        STRING email
        STRING password
        STRING nombre
        STRING telefono
        STRING alergenos
        STRING rol
        BOOLEAN enabled
        DATETIME fecha_creacion
        DATETIME ultimo_acceso
        LONG restaurante_trabajo_id FK
    }

    LOCALES {
        LONG id PK
        STRING nombre
        STRING direccion
        INT capacidad
        INT capacidad_comida
        INT capacidad_cena
        STRING horario
        TIME apertura_comida
        TIME cierre_comida
        TIME apertura_cena
        TIME cierre_cena
        DOUBLE latitud
        DOUBLE longitud
        DOUBLE pos_x
        DOUBLE pos_y
        STRING imagen_url
        LONG propietario_id FK
    }

    MENUS {
        LONG id PK
        STRING nombre
        STRING descripcion
        DECIMAL precio
        STRING ingredientes
        STRING alergenos
        TEXT primer_plato
        TEXT primer_plato_desc
        TEXT primer_plato_ingredientes
        TEXT segundo_plato
        TEXT segundo_plato_desc
        TEXT segundo_plato_ingredientes
        TEXT postre
        TEXT postre_desc
        TEXT postre_ingredientes
        BOOLEAN disponible
        LONG local_id FK
        LONG propietario_id FK
    }

    RESERVAS {
        LONG id PK
        LONG usuario_id FK
        LONG local_id FK
        LONG menu_id FK
        DATETIME fecha_hora
        INT numero_personas
        STRING estado
        STRING observaciones
        BOOLEAN asistencia_confirmada
        DATETIME fecha_creacion
        DATETIME fecha_actualizacion
        STRING nombre_invitado
        STRING email_invitado
        STRING telefono_invitado
        STRING stripe_payment_intent_id
        STRING estado_pago
    }

    RESENAS {
        LONG id PK
        LONG usuario_id FK
        LONG local_id FK
        INT puntuacion
        STRING comentario
        DATETIME fecha_creacion
    }

    RESENAS_MENU {
        LONG id PK
        LONG usuario_id FK
        LONG menu_id FK
        INT puntuacion
        STRING comentario
        DATETIME fecha_creacion
    }

    MENU_PROGRAMACION {
        LONG id PK
        LONG menu_id FK
        LONG local_id FK
        DATE fecha
    }

    SUGERENCIAS {
        LONG id PK
        LONG usuario_id FK
        STRING asunto
        STRING mensaje
        STRING estado
        STRING respuesta
        DATETIME fecha_creacion
        DATETIME fecha_actualizacion
    }

    VERIFICATION_TOKENS {
        LONG id PK
        STRING token
        LONG user_id FK
        DATETIME expiry_date
        STRING type
    }

    USUARIOS ||--o{ LOCALES : propietario
    USUARIOS }o--|| LOCALES : trabaja_en
    LOCALES ||--o{ MENUS : contiene
    USUARIOS ||--o{ MENUS : propietario
    USUARIOS ||--o{ RESERVAS : realiza
    LOCALES ||--o{ RESERVAS : recibe
    MENUS ||--o{ RESERVAS : opcional
    USUARIOS ||--o{ RESENAS : escribe
    LOCALES ||--o{ RESENAS : recibe
    USUARIOS ||--o{ RESENAS_MENU : escribe
    MENUS ||--o{ RESENAS_MENU : recibe
    LOCALES ||--o{ MENU_PROGRAMACION : agenda
    MENUS ||--o{ MENU_PROGRAMACION : programado
    USUARIOS ||--o{ SUGERENCIAS : envía
    USUARIOS ||--|| VERIFICATION_TOKENS : verifica
```

## Notas de cardinalidad y reglas
- Un usuario con rol CEO puede ser propietario de múltiples locales.
- Un empleado pertenece como máximo a un solo local (relación N:1 entre USUARIOS y LOCALES).
- Un menú puede estar asociado a un local o ser general (local_id nullable).
- Una reserva puede ser de invitado (usuario_id nullable) o de usuario registrado.
- La tabla RESENAS_MENU tiene restricción de unicidad (usuario_id, menu_id).
- La programación de menú (MENU_PROGRAMACION) limita un menú por local y fecha.
