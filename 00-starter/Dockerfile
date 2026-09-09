# Stage 1: Build the application JAR
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /build

# Cache dependencies layer
COPY pom.xml .
RUN mvn dependency:go-offline -B || true

# Copy source code and build package
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Lightweight production runtime image
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Run as non-root user for security (CIS compliance)
RUN groupadd -r spring && useradd -r -g spring spring
USER spring:spring

# Copy compiled JAR from builder stage
COPY --from=builder --chown=spring:spring /build/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
