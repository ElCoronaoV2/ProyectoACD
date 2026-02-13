# Diagrama de Clases UML

## Diagrama (Mermaid)
```mermaid
classDiagram
    class UserEntity {
        +Long id
        +String email
        +String password
        +String nombre
        +String telefono
        +String alergenos
        +Role rol
        +boolean enabled
        +LocalDateTime fechaCreacion
        +LocalDateTime ultimoAcceso
        +List~LocalEntity~ restaurantesPropios
        +LocalEntity restauranteTrabajo
    }

    class LocalEntity {
        +Long id
        +String nombre
        +String direccion
        +Integer capacidad
        +Integer capacidadComida
        +Integer capacidadCena
        +String horario
        +LocalTime aperturaComida
        +LocalTime cierreComida
        +LocalTime aperturaCena
        +LocalTime cierreCena
        +Double latitud
        +Double longitud
        +Double posX
        +Double posY
        +String imagenUrl
        +Double valoracion
        +UserEntity propietario
    }

    class MenuEntity {
        +Long id
        +String nombre
        +String descripcion
        +BigDecimal precio
        +String ingredientes
        +String alergenos
        +String primerPlato
        +String primerPlatoDesc
        +String primerPlatoIngredientes
        +String segundoPlato
        +String segundoPlatoDesc
        +String segundoPlatoIngredientes
        +String postre
        +String postreDesc
        +String postreIngredientes
        +LocalEntity local
        +UserEntity propietario
        +boolean disponible
        +Double valoracionMedia
    }

    class ReservaEntity {
        +Long id
        +UserEntity usuario
        +LocalEntity local
        +MenuEntity menu
        +LocalDateTime fechaHora
        +Integer numeroPersonas
        +EstadoReserva estado
        +String observaciones
        +Boolean asistenciaConfirmada
        +LocalDateTime fechaCreacion
        +LocalDateTime fechaActualizacion
        +String nombreInvitado
        +String emailInvitado
        +String telefonoInvitado
        +String stripePaymentIntentId
        +String estadoPago
    }

    class ResenaEntity {
        +Long id
        +UserEntity usuario
        +LocalEntity local
        +Integer puntuacion
        +String comentario
        +LocalDateTime fechaCreacion
    }

    class MenuResenaEntity {
        +Long id
        +UserEntity usuario
        +MenuEntity menu
        +Integer puntuacion
        +String comentario
        +LocalDateTime fechaCreacion
    }

    class MenuScheduleEntity {
        +Long id
        +MenuEntity menu
        +LocalEntity local
        +LocalDate fecha
    }

    class SugerenciaEntity {
        +Long id
        +UserEntity usuario
        +String asunto
        +String mensaje
        +EstadoSugerencia estado
        +String respuesta
        +LocalDateTime fechaCreacion
        +LocalDateTime fechaActualizacion
    }

    class VerificationToken {
        +Long id
        +String token
        +UserEntity user
        +LocalDateTime expiryDate
        +String type
    }

    class Role {
        <<enumeration>>
        DIRECTOR
        CEO
        EMPLEADO
        USER
        ADMIN
    }

    UserEntity "1" --> "0..*" LocalEntity : propietario
    UserEntity "0..1" --> "0..*" LocalEntity : restauranteTrabajo
    LocalEntity "1" --> "0..*" MenuEntity : menus
    UserEntity "1" --> "0..*" MenuEntity : propietario
    UserEntity "0..*" --> "0..*" ReservaEntity : reservas
    LocalEntity "1" --> "0..*" ReservaEntity : reservas
    MenuEntity "0..1" --> "0..*" ReservaEntity : reservas
    UserEntity "1" --> "0..*" ResenaEntity : reseñas
    LocalEntity "1" --> "0..*" ResenaEntity : reseñas
    UserEntity "1" --> "0..*" MenuResenaEntity : reseñas_menu
    MenuEntity "1" --> "0..*" MenuResenaEntity : reseñas_menu
    LocalEntity "1" --> "0..*" MenuScheduleEntity : programación
    MenuEntity "1" --> "0..*" MenuScheduleEntity : programación
    UserEntity "1" --> "0..*" SugerenciaEntity : sugerencias
    UserEntity "1" --> "1" VerificationToken : verificación
    UserEntity --> Role
```

## Notas
- Las clases reflejan las entidades JPA en el backend.
- Las asociaciones indican relaciones de dominio relevantes para la lógica de negocio.
