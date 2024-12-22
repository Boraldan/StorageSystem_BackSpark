FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /app
COPY ./pom.xml ./
COPY ./src ./src
RUN mvn -f pom.xml clean package -DskipTests -e

FROM openjdk:17
COPY --from=build /app/target/*-SNAPSHOT.jar /app/app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]



