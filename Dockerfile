FROM mcr.microsoft.com/openjdk/jdk:25-ubuntu AS build
WORKDIR /app

COPY gradlew .
COPY gradle ./gradle
COPY build.gradle settings.gradle ./

RUN chmod +x gradlew

COPY src ./src

RUN ./gradlew clean bootJar -x test

FROM mcr.microsoft.com/openjdk/jdk:25-ubuntu
WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]