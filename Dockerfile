FROM ubuntu:latest
LABEL authors="marianapires"

FROM openjdk:24-jdk-slim AS builder
WORKDIR /app
COPY mvnw pom.xml ./
COPY .mvn .mvn
COPY src src
RUN ./mvnw clean package -DskipTests


FROM openjdk:24-jdk-slim
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
