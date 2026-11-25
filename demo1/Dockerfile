# syntax=docker/dockerfile:1

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

COPY target/demo1-0.0.1-SNAPSHOT.jar app.jar

ENV TZ=Asia/Ho_Chi_Minh
ENTRYPOINT ["java", "-Duser.timezone=Asia/Ho_Chi_Minh", "-jar", "app.jar"]