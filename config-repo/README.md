# config-repo

Configuración que sirve `config-server` (backend `native`). Un archivo base por proyecto (`auth-service.properties`, `api-gateway.properties`...) y uno por perfil (`auth-service-dev.properties`); el del perfil gana al base. `application.properties` se aplica a todos.

| Archivo | Qué hay |
|---|---|
| `api-gateway.properties` | rutas, URLs de los servicios, timeouts, CORS, rutas públicas |
| `auth-service.properties` | puerto, base, URL de customer-service y timeouts de Feign (registro) |
| `customer-service.properties` | puerto, base, URL de auth-service, timeouts de Feign y parámetros de Retry y Circuit Breaker |
| `account-service.properties` | puerto y base |
| `*-dev.properties` | datos de prueba (Flyway `db/seed`), SQL visible y logs en DEBUG |
| `*-docker.properties` | solo con `docker compose --profile app`: `mysql:3306` y `http://<servicio>:<puerto>` en lugar de `localhost` |

Las URL de las bases usan `${DB_PORT:3307}`: cada servicio lo resuelve con su `.env`. En Docker no: dentro de la red de Compose MySQL escucha en `mysql:3306`, y el perfil `docker` (activo con `dev`, `SPRING_PROFILES_ACTIVE=dev,docker`) gana porque es el último.

Aquí **no** van secretos: `JWT_SECRET`, `DB_USERNAME` y `DB_PASSWORD` llegan a cada servicio por variables de entorno o `.env`. `ConfigServerTest` falla si aparece una clave con `password`, `secret` o `token`.

Para ver lo que recibe un servicio: `curl localhost:8888/auth-service/dev`. Tras cambiar un valor basta con reiniciar el servicio.
