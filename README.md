# Microservice Samples
Demonstrates how to write the same sample application with different technologies.

## Application

Applications implemented with different technology stacks can be found in the subfolders. Please name each application folder by listing the most essential technologies (for example: spring-boot-and-spring-boot-data-jpa)

### API
All applications must implement a CRUD-style REST-api according to [this swagger specification.](https://github.com/JorgenRingen/microservice-samples/blob/master/swagger.yml)

<img src="https://imgur.com/a/wD3zRsz" width="30%" height="30%">

### Domain
The domain model of the consists of two entities:

```
Employee
	- id : number
	- firstname : string
	- lastname : string
	- dateOfBirth - date

Company
	- id : number
	- name : string
	- employees : Set<Employee>
```

[todo uml-diagram?]

### Database
The application should connect to a PostgreSQL database created with the following schema: [todo]

There is a docker-image initialised with the schema in the `./db` directory which can be started like this:
```bash
cd ./db
docker build -t ms-samples/db .
docker run -p 5432:5432 ms-samples/db
```

- port: 5432
- database: companies
- username: user
- password: secret

[todo verify]

### Testing
Tests for the REST-api can be found in the `./tests` directory. The folder contains a postman test-suite that can be executed command-line with newman. The test-suite verifies all operations of the API and the functional flow. 

[todo mekk]

## Application Principles
Each application should be implemented according to the relevant principles in [The Twelve Factor Aps](https://12factor.net/). 

The motivation here is to provide somewhat production-ready sample applications, so here are some loosely defined requirements for each application:
- Contain a README.md that explains how to build and run the application and pre-requisites
- Configuration should be overridable by the environment and not  hardcoded in the application
- Should implement some form of logging
- Should have some form of testing (integration tests and/or junit tests)
- Should preferably have a Dockerfile so the application can be built and run as a Docker container
