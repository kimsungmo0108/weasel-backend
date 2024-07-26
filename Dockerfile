# FROM openjdk:21-jdk-slim
FROM chainguard/jre:latest


WORKDIR /app


# COPY gradle/ ./gradle/
# COPY gradlew .
# COPY gradlew.bat .
# COPY settings.gradle .


# COPY app/build.gradle ./app/build.gradle
# COPY app/src ./app/src


# COPY app/build/libs/app.jar app.jar
COPY ./app/build/libs/app.jar .


EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]