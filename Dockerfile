FROM ubuntu:latest
LABEL authors="marianapires"

FROM openjdk:24-jdk-slim AS builder
WORKDIR /app
RUN apt-get update && apt-get install -y bash && rm -rf /var/lib/apt/lists/*
RUN chmod +x mvnw || true
COPY mvnw pom.xml ./
COPY .mvn .mvn
COPY src src
RUN ./mvnw clean package -DskipTests


FROM openjdk:24-jdk-slim
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
