FROM eclipse-temurin:21-jdk-alpine
LABEL authors="f4"
VOLUME /tmp
COPY target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]