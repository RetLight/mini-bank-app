# account-service

Cuentas, transferencias y movimientos. Puerto **8083**, base de datos **`account_db`**. Ver el [README general](../README.md).

## Qué hace

- `/transfers` y `/movements`: transferencias desde las cuentas del cliente del token y sus movimientos.
- Solo **valida** tokens (`common.security.JwtParser`, con JJWT). Cada cuenta se comprueba contra el claim `customerId`.
- El código conserva el paquete `movement` del monolito.

## Añadido con el gateway y el Config Server

- La regla `/admin/**` exige `ADMIN` (todavía sin endpoints de administración).
- Configuración desde config-server (`config-repo/account-service*.properties`); errores en el formato `ErrorResponse`.

## Base de datos

`account`, `transfer` y `movement` (`V1__create_account_transfer_movement.sql`). `account.customer_id` no tiene clave foránea, pero sí un índice. Los datos de prueba están en `db/seed/R__seed_dev_data.sql` y solo se cargan con el perfil `dev`.

## Arrancar y probar

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
./mvnw test        # H2 en memoria: mismas migraciones y datos de prueba, sin MySQL
```

Arranca después de config-server. Necesita `JWT_SECRET`, `DB_USERNAME` y `DB_PASSWORD`, que lee del `.env` de la raíz del repositorio.
