# CLEAN CRM API

A CRM API developed using [Spring Framework](https://spring.io/) and following a [Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html) approach.

Still a work in progress...

## Features

- [Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [Java](https://www.java.com/en/) + [Spring Boot](https://spring.io/projects/spring-boot)
- [PostgreSQL](https://www.postgresql.org/) as relational DB
- [Redis](https://redis.io/) for session management (speed and horizontal scalability)
- [GCP Cloud Storage](https://cloud.google.com/storage?hl=en) for image storage
- Fully containerized development environment using [Docker](https://www.docker.com/), [DockerCompose](https://docs.docker.com/compose/) and [Testcontainers](https://testcontainers.com/)

## Get started

### Run the test suite

```sh
./gradlew test
```

or using [GNU Make](https://www.gnu.org/software/make/):

```sh
make test
```

### Start the development server

```sh
./gradlew bootRun --args='--spring.profiles.active=dev'
```

or using [GNU Make](https://www.gnu.org/software/make/):

```sh
make run
```

### Have fun

Visit [the OpenAPI documentation](http://localhost:8080/swagger-ui/index.html)

And send some requests (using for example [HTTPie CLI](https://httpie.io/)):

```sh
http --form POST http://localhost:8080/v1/login \
  Content-Type:application/x-www-form-urlencoded \
  username=admin \
  password=secret

```
