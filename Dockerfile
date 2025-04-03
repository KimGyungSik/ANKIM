# 🔧 1단계: Build Stage
FROM --platform=linux/amd64 openjdk:17-jdk-slim AS build

# 작업 디렉토리 설정
WORKDIR /app

# 프로젝트 소스 전체 복사
COPY . .

# JAR 생성 (테스트는 실행하지 않음)
RUN ./gradlew bootJar -x test

# 🚀 2단계: Runtime Stage
FROM --platform=linux/amd64 openjdk:17-jdk-slim

WORKDIR /app

# application.yml 필요 시 복사 (선택사항)
COPY src/main/resources/application.yml /app/application.yml

# 빌드된 JAR 복사
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
