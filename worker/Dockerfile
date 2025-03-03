FROM maven:3.9.8-eclipse-temurin-21 AS builder
WORKDIR /build
COPY ../worker/pom.xml .
COPY ../worker/src ./src

RUN mvn clean install

FROM eclipse-temurin:21.0.3_9-jre
WORKDIR /app
COPY --from=builder /build/target/worker.jar app.jar
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
