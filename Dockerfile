# Etapa 1: Build
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /dealer
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Runtime
FROM eclipse-temurin:21-jre
WORKDIR /dealer
COPY --from=builder /dealer/target/dealer-0.0.1-SNAPSHOT.jar dealer.jar

EXPOSE 8080

# Ejecutar como usuario no root
RUN useradd -m springuser
USER springuser

ENTRYPOINT ["java", "-jar", "dealer.jar"]
