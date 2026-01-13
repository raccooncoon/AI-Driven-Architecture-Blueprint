# Build stage
FROM gradle:8-jdk17 AS build
WORKDIR /app

# Copy gradle files
COPY build.gradle.kts .
COPY settings.gradle.kts .

# Pre-fetch dependencies
RUN gradle dependencies --no-daemon || true

# Copy source
COPY src src

# Build application
RUN gradle bootJar --no-daemon

# Run stage
FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
