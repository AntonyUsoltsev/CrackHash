FROM maven:3.9.8-eclipse-temurin-21 AS builder
WORKDIR /build
COPY ../manager/pom.xml .
COPY ../manager/src ./src

RUN mvn clean install

FROM eclipse-temurin:21.0.3_9-jre
WORKDIR /app
COPY --from=builder /build/target/manager.jar app.jar
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
