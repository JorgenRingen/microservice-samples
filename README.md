# Microservice Samples
Shows how a simple sample-application can be implemented with different technology stacks.

## Application

Implementations can be found in the subfolders. Please name the application folder by listing the most essential technologies separated by underscore (for example: spring-boot_spring-data-jpa_spring-web). If the application is "work in progress", please postfix the folder name with "wip_"

### API
All applications must implement a CRUD-style REST-api according to [this swagger specification.](https://github.com/JorgenRingen/microservice-samples/blob/master/swagger.yml)

<img src="https://i.imgur.com/wPQ0GYz.png" width="75%" height="75%">

### Domain
The domain model consists of two entities:

<img src="https://i.imgur.com/GA2TNb3.png" width="50%" height="50%">

### Database
The application should connect to a PostgreSQL database created with [this schema](https://github.com/JorgenRingen/microservice-samples/blob/master/db/init.sql).


[A postgresql docker-image initialised with the schema is available on hub.docker.com](https://hub.docker.com/r/jorgenringen/ms_samples_postgres/). Start the container by running: `docker run -p 5432:5432 jorgenringen/ms_samples_postgres`

```
- port: 5432
- database: companies
- username: user
- password: password
```

### Testing
A JUnit testsuite for the REST-api can be found in the `./tests` directory. The test-suite verifies all operations of the API and the functional flow. Tests can be executed by running `mvn test` in the `./tests` directory. 

## Application Principles
Each application should be implemented according to relevant principles in [The Twelve Factor Aps](https://12factor.net/). 

The motivation here is to provide somewhat production-ready sample applications, so here are some loosely defined requirements for each application:
- Contain a README.md that explains how to build and run the application and pre-requisites
- Configuration should be overridable by the environment and not hardcoded in the application
- Should have some form of testing (integration tests and/or junit tests)
- Should preferably have a Dockerfile so the application can be built and run as a Docker container
- Contain a buildAndRun.sh that builds and starts the application

## Contribution
Please contribute pull-requests with your unique technology stack and implementation :-)
