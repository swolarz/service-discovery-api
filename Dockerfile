FROM maven:3.6-jdk-11-slim AS build

WORKDIR /usr/src/app

COPY pom.xml ./
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn package


FROM openjdk:11-jre-slim

COPY --from=build /usr/src/app/target/service-discovery-api-*.jar /usr/bin/service-discovery-api.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/usr/bin/service-discovery-api.jar"]
