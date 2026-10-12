# Stage 1: Build the application using Maven and OpenJDK 17
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Build application artifact
COPY src ./src
COPY docs ./docs
RUN mvn clean package -DskipTests

# Compile fallback provided class
COPY provided.java ./provided.java
RUN javac provided.java

# Stage 2: Minimal JRE runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy built jar and fallback runner from build stage
COPY --from=build /app/target/foodiehub-1.0.0.jar app.jar
COPY --from=build /app/provided.class /app/provided.class

# Copy entrypoint script
COPY entrypoint.sh /app/entrypoint.sh
RUN chmod +x /app/entrypoint.sh

ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["/app/entrypoint.sh"]
