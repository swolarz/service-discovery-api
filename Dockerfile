FROM openjdk:11-jdk-slim AS build
COPY . /usr/app

WORKDIR /usr/app

RUN ./mvnw clean package

FROM openjdk:11-jre-slim
COPY --from=build /usr/app/target/service-discovery-api-0.0.1-SNAPSHOT.jar /usr/bin/service-discovery-api.jar

EXPOSE 8090
WORKDIR /usr/bin

ENTRYPOINT ["java", "-jar", "/usr/bin/service-discovery-api.jar"]
