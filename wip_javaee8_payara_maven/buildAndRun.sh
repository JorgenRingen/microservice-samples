#!/bin/sh
mvn clean package && docker build -t org.example/demoapp .
docker rm -f demoapp || true && docker run -d -p 8080:8080 -p 4848:4848 --name demoapp org.example/demoapp 
