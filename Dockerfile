# ---- Build stage ----
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app

# Cache dependencies first
COPY pom.xml ./
RUN mvn -B -q -DskipTests dependency:go-offline

# Copy source and build
COPY src ./src
RUN mvn -B -DskipTests clean package

# ---- Runtime stage ----
FROM eclipse-temurin:17-jre
ENV TZ=UTC \
    JAVA_OPTS="" \
    SPRING_PROFILES_ACTIVE=default

WORKDIR /app

# Create non-root user
RUN useradd -r -s /sbin/nologin spring && \
    mkdir -p /app && chown -R spring:spring /app

# Copy the built jar
COPY --from=builder /app/target/*.jar /app/app.jar

# Expose default Spring Boot port
EXPOSE 8080

USER spring

# Healthcheck (optional): expects actuator if enabled; otherwise relies on container liveness
# HEALTHCHECK --interval=30s --timeout=5s --start-period=30s CMD wget -qO- http://localhost:8080/actuator/health || exit 1

# Allow passing JVM opts via JAVA_OPTS and Spring config via env vars
# Examples:
#  -e SPRING_DATA_MONGODB_URI=mongodb://mongo:27017/inventory
#  -e SERVER_PORT=8080
#  -e SPRING_PROFILES_ACTIVE=prod
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
