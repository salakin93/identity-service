# ---------- BUILD STAGE ----------
FROM gradle:8.7-jdk21 AS builder

WORKDIR /app

COPY build.gradle settings.gradle ./
COPY gradle ./gradle
RUN gradle dependencies --no-daemon

COPY . .

RUN gradle bootJar --no-daemon

# ---------- RUNTIME STAGE ----------
FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java","-jar","/app/app.jar"]