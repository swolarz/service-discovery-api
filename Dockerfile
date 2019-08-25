FROM openjdk:11-jdk-slim AS build

WORKDIR /usr/src/app

COPY .mvn ./.mvn
COPY mvnw ./

COPY pom.xml ./

RUN ./mvnw dependency:go-offline

COPY src ./src

RUN ./mvnw package


FROM openjdk:11-jre-slim

COPY --from=build /usr/src/app/target/service-discovery-api-*.jar /usr/bin/service-discovery-api.jar

EXPOSE 8090
ENTRYPOINT ["java", "-jar", "/usr/bin/service-discovery-api.jar"]
