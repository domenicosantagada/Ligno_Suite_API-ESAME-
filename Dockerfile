# ==========================================
# Fase 1: Build dell'applicazione
# ==========================================
FROM gradle:8.5-jdk21 AS builder
WORKDIR /app

# Copia i file necessari per la build
COPY build.gradle settings.gradle ./
COPY gradle ./gradle
COPY gradlew ./
COPY src ./src

# Esegue la build saltando i test per velocizzare il processo
RUN ./gradlew build -x test

# ==========================================
# Fase 2: Esecuzione
# ==========================================
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copia solo il file .jar generato dalla fase precedente
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
