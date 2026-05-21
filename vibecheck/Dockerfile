# syntax=docker/dockerfile:1

# ---- Build stage ----
FROM eclipse-temurin:25-jdk AS build
WORKDIR /app

# Resolve dependencies first so this layer is cached unless pom.xml changes
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw -B dependency:go-offline

# Build the executable jar (tests already ran in CI)
COPY src/ src/
RUN ./mvnw -B clean package -DskipTests

# ---- Runtime stage ----
FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Keep the JVM inside the container memory limit (Render free tier = 512 MB)
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0"

# Render assigns the listening port via $PORT.
# The dev profile otherwise binds 127.0.0.1:8080, which is unreachable on Render,
# so we override server.address/server.port on the command line (highest precedence).
ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar --server.address=0.0.0.0 --server.port=$PORT"]
