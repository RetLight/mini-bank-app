# customer-service

Clientes y beneficiarios favoritos. Puerto **8082**, base de datos **`customer_db`**. Ver el [README general](../README.md).

## Qué hace

- `/favorites`: alta, listado, detalle y búsqueda por alias de los favoritos del cliente del token.
- Solo **valida** tokens (`common.security.JwtParser`, con JJWT): no tiene login ni codificador de contraseñas.
- El cliente sale del claim `customerId`; no consulta a auth-service.

## Añadido con el gateway y el Config Server

- `PUT` y `DELETE /favorites/{id}`: CRUD completo, solo del dueño (404 si es de otro).
- `GET /admin/customers`: todos los clientes, solo `ADMIN`.
- `POST /internal/customers`: alta de cliente durante el registro, solo para el rol `SERVICE`.
- Al crear un favorito consulta a auth-service (`GET /users/me`) con Retry, Circuit Breaker y fallback: si auth no responde, el favorito se guarda con `ownerVerified=false` (migración `V2`).
- Configuración desde config-server (`config-repo/customer-service*.properties`); errores en el formato `ErrorResponse`.

## Base de datos

`customer` y `favorite` (`V1__create_customer_and_favorite.sql`), con la clave foránea `favorite → customer`. Los datos de prueba están en `db/seed/R__seed_dev_data.sql` y solo se cargan con el perfil `dev`.

## Arrancar y probar

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
./mvnw test        # H2 en memoria: mismas migraciones y datos de prueba, sin MySQL
```

Arranca después de config-server. Necesita `JWT_SECRET`, `DB_USERNAME` y `DB_PASSWORD`, que lee del `.env` de la raíz del repositorio.
