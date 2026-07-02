FROM gradle:8.12.1-jdk17 AS builder

WORKDIR /workspace

COPY settings.gradle build.gradle ./
COPY gradle ./gradle
COPY gradlew ./
COPY src ./src

RUN ./gradlew clean bootJar --no-daemon

FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=builder /workspace/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
