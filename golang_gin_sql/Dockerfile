FROM golang:alpine

WORKDIR /go/src/github.com/di0nys1us/microservice-samples/golang_gin_sql
COPY . .

RUN apk add --no-cache git mercurial
RUN go get -d -v ./...
RUN go install -v ./...
RUN apk del git mercurial

CMD ["server"]
