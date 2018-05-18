#!/bin/sh
docker build -t ms-samples/db .
docker run -p 5432:5432 ms-samples/db