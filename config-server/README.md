# config-server

Spring Cloud Config Server con backend `native`, puerto **8888**. Sirve la carpeta [`config-repo/`](../config-repo/), por defecto `../config-repo`; `CONFIG_REPO_PATH` la cambia. Arráncalo **primero**. Ver el [README general](../README.md).

El perfil `native` se añade con `spring.profiles.include`: así el servidor arranca aunque `SPRING_PROFILES_ACTIVE=dev` esté en el entorno.

```bash
./mvnw spring-boot:run
curl localhost:8888/auth-service/dev     # lo que recibe auth-service con el perfil dev
./mvnw test                              # comprueba config-repo: perfiles, archivos de cada servicio y que no hay secretos
```
