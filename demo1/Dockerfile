# syntax=docker/dockerfile:1

# ===== BUILD STAGE =====
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app

# Chỉ copy pom.xml trước để cache layer dependency
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -U dependency:go-offline \
      -Dmaven.wagon.http.retryHandler.count=5 \
      -Dmaven.wagon.httpconnectionManager.ttlSeconds=120 \
    || (sleep 10 && mvn -B -U dependency:go-offline \
      -Dmaven.wagon.http.retryHandler.count=5)

# Sau đó mới copy source
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B clean package -DskipTests \
      -Dmaven.wagon.http.retryHandler.count=5 \
      -Dmaven.wagon.httpconnectionManager.ttlSeconds=120

# ===== RUN STAGE =====
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

RUN mkdir -p /app/credentials
COPY image-1acd5-firebase-adminsdk-eqvw5-a3fa3e0e84.json /app/credentials/firebase.json

ENV TZ=Asia/Ho_Chi_Minh
ENTRYPOINT ["java", "-Duser.timezone=Asia/Ho_Chi_Minh", "-jar", "app.jar"]