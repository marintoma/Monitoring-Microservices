FROM eclipse-temurin:25-jdk-alpine AS builder
WORKDIR /app
COPY . .
ARG SERVICE_NAME
RUN ./mvnw clean package -DskipTests -pl ${SERVICE_NAME} -am

FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
ARG SERVICE_NAME
COPY --from=builder /app/${SERVICE_NAME}/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]