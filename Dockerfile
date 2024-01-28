# syntax=docker/dockerfile:1.4
FROM eclipse-temurin:21.0.2_13-jdk-alpine as build
WORKDIR /workspace/app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src
RUN ./mvnw install -DskipTests

ARG JAR_FILE=target/*.jar
COPY --link ${JAR_FILE} target/application.jar
RUN java -Djarmode=layertools -jar target/application.jar extract --destination target/extracted

FROM eclipse-temurin:21.0.2_13-jdk-alpine
RUN addgroup -S laabhumadm && adduser -S laabhumadm -G laabhumadm
VOLUME /tmp
USER laabhumadm
ARG EXTRACTED=/workspace/app/target/extracted
WORKDIR application
COPY --from=build ${EXTRACTED}/dependencies/ ./
COPY --from=build ${EXTRACTED}/spring-boot-loader/ ./
COPY --from=build ${EXTRACTED}/snapshot-dependencies/ ./
COPY --from=build ${EXTRACTED}/application/ ./
ENTRYPOINT ["java","-XX:TieredStopAtLevel=1","-Dspring.main.lazy-initialization=true","org.springframework.boot.loader.launch.JarLauncher"]