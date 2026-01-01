# ===== BUILD STAGE =====
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# ===== RUN STAGE =====
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app

# copy jar
COPY --from=build /app/target/*.jar app.jar

# tạo folder linux
RUN mkdir -p /app/credentials

# copy firebase json vào linux path
COPY image-1acd5-firebase-adminsdk-eqvw5-a3fa3e0e84.json /app/credentials/firebase.json

ENTRYPOINT ["java", "-jar", "app.jar"]