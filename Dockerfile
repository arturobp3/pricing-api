FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=builder /app/target/pricing-api-0.0.1-SNAPSHOT.jar app.jar

ENV JAVA_OPTS="-DAPP_ENV=prod -DAPP_REGION=es"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
