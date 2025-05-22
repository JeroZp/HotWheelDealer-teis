# Etapa 1: Build
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /dealer
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests && cp target/*.jar app.jar

# Etapa 2: Runtime
FROM eclipse-temurin:21-jre
WORKDIR /dealer
COPY --from=builder /dealer/app.jar app.jar
EXPOSE 8080

# Ejecutar como usuario no root
RUN useradd -m springuser
USER springuser

ENTRYPOINT ["java", "-jar", "app.jar"]
