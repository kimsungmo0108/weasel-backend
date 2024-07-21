# Build
FROM gradle:8.5-jdk21 AS build


WORKDIR /app

# Gradle Wrapper 및 Gradle 설정 파일 복사
COPY gradle/ gradle/
COPY gradlew .
COPY gradlew.bat .
COPY settings.gradle .
COPY app/build.gradle /app/build.gradle 
COPY app/ /app/  

# Gradle 의존성 설치 및 애플리케이션 빌드
RUN ./gradlew build --no-daemon

# 실행
FROM openjdk:21-jdk-slim

# 작업 디렉토리 설정
WORKDIR /app

# 빌드 단계에서 생성된 JAR 파일 복사
ARG JAR_FILE=build/libs/*.jar
COPY --from=build /app/${JAR_FILE} app.jar

# 포트 노출
EXPOSE 8080

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
