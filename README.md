# Service Discovery REST API

## Running the service

#### Requirements
* Docker
* Docker Compose

#### Starting application

`docker-compose up --build`

After running the command, the service should available at port **8090**.

#### Running integration tests

`./mvnw verify`

(launches necessary docker containers)


`./mvnw docker:stop`

(stops running containers from previous IT tests; may be needed in case of subsequent IT test launches)
