# syntax=docker/dockerfile:1
# Imagen del servicio en dos etapas: se compila con el JDK y se ejecuta solo con el JRE, sin Maven ni código fuente.
# El mismo Dockerfile está en los cinco proyectos: cada uno sigue siendo independiente (ver README).

FROM eclipse-temurin:21-jdk AS build
WORKDIR /build
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
COPY src/ src/
# sed: en Windows mvnw puede estar con CRLF y bash no lo ejecuta. La caché de ~/.m2 se comparte entre builds.
# Los tests no se ejecutan aquí: se ejecutan con ./mvnw test antes de construir.
RUN --mount=type=cache,target=/root/.m2 \
    sed -i 's/\r$//' mvnw && chmod +x mvnw \
    && ./mvnw -B -q -DskipTests package \
    && cp target/*.jar /build/app.jar

FROM eclipse-temurin:21-jre
RUN useradd --system --uid 10001 minibank
WORKDIR /app
COPY --from=build /build/app.jar app.jar
# Sin root: si alguien compromete el proceso, no es root dentro del contenedor.
USER minibank
# La JVM toma hasta el 75 % de la memoria del contenedor, no la de la máquina.
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75"
ENTRYPOINT ["java", "-jar", "app.jar"]
