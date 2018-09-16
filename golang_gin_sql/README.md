# Golang / Gin / SQL implementation

This version of the microservice samples showcase use of the
[Go (Golang) programming language from Google](https://golang.org/). It uses the [Gin web framework](https://gin-gonic.github.io/gin/) for construction of the RESTful API. Data storage is implemented using vanilla database/sql from the Go standard library.

## Prerequisites

To be able to develop and build Go applications you need to download and configure the Go developer tools. Instructions and downloads for your system can be found [here](https://golang.org/dl/). 

Most Linux distributions has prebuilt packages for Go available. The easiest way to get started writing Go applications is to install the correct package from the official repositories of your distribution. On Arch and derivatives you can just do `pacman -S go` or on Ubuntu `apt-get install golang`. If you are on macOS and have Homebrew installed you can install the Go tools with `brew install go`.

For working with Go code I can recommend just using Visual Studio Code with the excellent [Go extension from Microsoft](https://code.visualstudio.com/docs/languages/go). You can also use a full fledged IDE like [Goland](https://www.jetbrains.com/go/) from Jetbrains or just install and enable the Go plugin in IntelliJ IDEA.

## Download and install the dependencies

```bash
$ go get -u ./...
```

## Download and install [Gin](https://github.com/codegangsta/gin)

I like to use a tool called Gin (not the web framework, but has the same name) when developing applications in Go. It is a live reload utility that reloads the application on every save and makes life easier.

```bash
$ go get -u github.com/codegangsta/gin
```

NB! Remember to add $GOPATH/bin ($GOBIN) to your $PATH.

## Start the server in development mode

The application uses some environment variables that are read from the .env file if available. They can be overridden in a Dockerfile, docker-compose file etc.

### Start the database with Docker Compose

```bash
$ docker-compose up -d
```

### Start the application in development mode

```bash
$ ./run
```

The API can now be accessed at [http://localhost:3000](http://localhost:3000).

## Build the application

```bash
$ ./build
```
OR
```bash
$ go install
```

The last command will place the executable in $GOPATH/bin ($GOPATH/bin/server).

## Build and run the application

```bash
$ ./buildAndRun.sh
```

The API can now be accessed at [http://localhost:3001](http://localhost:3001).

## Build and run the application using Docker Compose

```bash
$ docker-compose -f docker-compose.ci.yml up -d
```

The API can now be accessed at [http://localhost:8080](http://localhost:8080).

## Run the tests

```bash
$ ./test
```
