# api-gateway

Punto único de entrada (Spring Cloud Gateway, WebFlux), puerto **8080**. Sus rutas, timeouts y CORS están en `config-repo/api-gateway*.properties`; sin Config Server no tiene rutas. Ver el [README general](../README.md).

| Ruta | Servicio |
|---|---|
| `/auth/**` | auth-service :8081 |
| `/favorites/**`, `/admin/customers/**` | customer-service :8082 |
| `/transfers/**`, `/movements/**` | account-service :8083 |
| `/internal/**` | sin ruta: solo entre servicios |

- `BearerRequiredFilter`: fuera de `/auth/**`, 401 si no hay `Authorization: Bearer ...`. No lee el token: lo valida cada servicio.
- `RequestIdFilter`: `X-Request-Id` hacia el servicio y de vuelta, una línea de log por petición; borra `X-Caller-Service` de las peticiones externas.
- `GatewayErrorHandler`: sus propios errores (404 sin ruta, 503 servicio caído, 504 timeout) con el mismo `ErrorResponse` que los servicios (`ErrorBodies`).

```bash
./mvnw spring-boot:run      # después de config-server
./mvnw test                 # servicios falsos con el HttpServer de la JDK
```
