# Mini-Bank-App

## ¿Qué es MiniBank?

MiniBank es un banco digital pequeño. Permite que una persona se registre como cliente, abra sus cuentas y maneje su dinero desde la aplicación, sin ir a una agencia.

Con MiniBank un cliente puede:

- **Registrarse** con su documento de identidad, nombre, correo y teléfono.
- **Ingresar a la app** con su usuario y contraseña. Si se equivoca varias veces seguidas, su acceso se bloquea para protegerlo.
- **Tener una o varias cuentas** en soles o en dólares, cada una con su número de cuenta y su CCI.
- **Transferir dinero** a otras cuentas de MiniBank o de otros bancos.
- **Guardar beneficiarios**, es decir, las cuentas a las que envía dinero seguido, para no escribir los datos cada vez.
- **Ver sus movimientos**: cada ingreso y cada salida de dinero queda registrado con el saldo que quedó después.

El proyecto está hecho con **Spring Boot** y sigue una **arquitectura hexagonal**.

## Modelo de datos (ERD)

- Tablas: [init.sql](init.sql)
- Datos de prueba: [test-data.sql](test-data.sql) (borra y vuelve a cargar los datos)

```mermaid
erDiagram
    CUSTOMER ||--|| APP_USER : "tiene"
    CUSTOMER ||--o{ ACCOUNT : "es dueño de"
    CUSTOMER ||--o{ FAVORITE : "registra"
    ACCOUNT ||--o{ MOVEMENT : "registra"
    ACCOUNT ||--o{ TRANSFER : "envía (origen)"
    ACCOUNT |o--o{ TRANSFER : "recibe (destino)"
    TRANSFER |o--o{ MOVEMENT : "genera"

    CUSTOMER {
        int id PK
        varchar document_type
        varchar document_number UK
        varchar first_names
        varchar last_names
        varchar email UK
        varchar phone
        varchar status
        timestamp registered_at
    }

    APP_USER {
        int id PK
        varchar username UK
        varchar password_hash
        varchar role
        int failed_attempts
        boolean blocked
        timestamp last_login
        int customer_id FK,UK
    }

    ACCOUNT {
        int id PK
        varchar account_number UK
        varchar cci UK
        varchar type
        varchar currency
        decimal balance
        varchar status
        timestamp opened_at
        int customer_id FK
    }

    TRANSFER {
        int id PK
        decimal amount
        varchar currency
        varchar status
        varchar rejection_reason
        timestamp requested_at
        timestamp processed_at
        varchar type
        varchar destination_cci
        varchar destination_bank
        varchar destination_holder
        int source_account_id FK
        int destination_account_id FK
    }

    MOVEMENT {
        int id PK
        varchar type
        decimal amount
        decimal resulting_balance
        timestamp occurred_at
        int account_id FK
        int transfer_id FK
    }

    FAVORITE {
        int id PK
        varchar alias
        varchar account_number
        varchar bank
        varchar holder
        int customer_id FK
    }
```

## Casos de uso implementados

Los endpoints `/api/me/...` requieren `Authorization: Bearer <token>`.

| Caso de uso | Endpoint | Descripción |
|---|---|---|
| Login | `POST /api/auth/login` | Valida usuario y contraseña y devuelve un token JWT. |
| Agregar favorito | `POST /api/me/favorites` | Guarda una cuenta de destino frecuente con un alias. |
| Listar favoritos | `GET /api/me/favorites` | Devuelve todos mis favoritos. |
| Ver favorito | `GET /api/me/favorites/{id}` | Devuelve un favorito por su id. |
| Buscar favorito por alias | `GET /api/me/favorites/alias/{alias}` | Devuelve un favorito por su alias. |
| Hacer transferencia | `POST /api/me/transfers` | Envía dinero desde una cuenta mía a otra cuenta de MiniBank o de otro banco. |
| Ver transferencia | `GET /api/me/transfers/{id}` | Devuelve una transferencia que envié o recibí. |
| Listar transferencias | `GET /api/me/transfers?accountId=` | Devuelve las transferencias enviadas desde una de mis cuentas. |
| Ver movimiento | `GET /api/me/movements/{id}` | Devuelve un movimiento de una de mis cuentas. |
| Listar movimientos | `GET /api/me/movements?accountId=` | Devuelve los ingresos y salidas de una de mis cuentas. |

## Cómo probar

- Usuarios de [test-data.sql](test-data.sql): `demo/demo123`, `admin/admin123`, `maria/maria123`, `pedro/pedro123` (bloqueado).