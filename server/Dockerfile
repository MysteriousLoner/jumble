# Multi-stage build for Java Spring Boot application
FROM maven:3.8.7-eclipse-temurin-8 AS build

WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src ./src
COPY mvnw .
COPY mvnw.cmd .
COPY .mvn ./.mvn
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:8-jre

WORKDIR /app

# Install curl for healthcheck
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Copy the built jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
