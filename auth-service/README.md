# auth-service

Usuarios, login y emisión del JWT. Puerto **8081**, base de datos **`auth_db`**. Ver el [README general](../README.md).

## Qué hace

- `POST /auth/login`: valida usuario y contraseña (BCrypt) y devuelve un JWT HS256 de 30 minutos.
- El token lleva `sub` (usuario), `roles` y `customerId`. Es el contrato con los otros dos servicios: si cambia, ellos también.
- Es el único servicio que **firma** tokens (`JwtTokenIssuer`, con JJWT); también los valida, con `common.security.JwtParser`.

## Añadido con el gateway y el Config Server

- `POST /auth/register`: crea el cliente en customer-service (Feign, `POST /internal/customers` con un token de servicio de rol `SERVICE`) y después el usuario; devuelve un token.
- `GET /users/me`: estado actual del usuario del token. Lo consulta customer-service al crear un favorito.
- Logs de llamadas: `ServiceCallFeignLogger` (las que hace) e `IncomingServiceCallFilter` (las que recibe).
- Configuración desde config-server (`config-repo/auth-service*.properties`); errores en el formato `ErrorResponse`.

## Base de datos

`app_user` (`V1__create_app_user.sql`). `customer_id` no tiene clave foránea: el cliente vive en customer-service. Los datos de prueba están en `db/seed/R__seed_dev_data.sql` y solo se cargan con el perfil `dev`.

## Arrancar y probar

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
./mvnw test        # H2 en memoria: mismas migraciones y datos de prueba, sin MySQL
```

Arranca después de config-server. Necesita `JWT_SECRET`, `DB_USERNAME` y `DB_PASSWORD`, que lee del `.env` de la raíz del repositorio.
