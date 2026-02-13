# Generación de JavaDoc

## Objetivo
Documentar el código fuente Java para describir clases, interfaces y métodos, aclarando intención y uso. Esta documentación ayuda al mantenimiento y onboarding del equipo.

## Comando de generación
Desde la raíz del proyecto backend:
- Maven Wrapper: `./mvnw -q -DskipTests javadoc:javadoc`

## Salida
La documentación se genera en:
- `tec/target/reports/apidocs/index.html`

## Buenas prácticas
- Añadir JavaDoc a clases de `controller`, `service`, `security` y `entity`.
- Documentar parámetros y valores de retorno (`@param`, `@return`).
- Mantener consistencia con la lógica de negocio actual.

## Checklist de entrega
- [ ] JavaDoc generado localmente.
- [ ] Verificación de que el índice carga correctamente.
- [ ] Revisar clases críticas (Auth, Reservas, Menús, Usuarios).
