package main

import (
	"log"
	"os"

	"github.com/joho/godotenv"

	"github.com/di0nys1us/microservice-samples/golang_gin_sql/http"
	"github.com/di0nys1us/microservice-samples/golang_gin_sql/sql"

	_ "github.com/lib/pq"
)

func main() {
	godotenv.Load()

	dbUser := os.Getenv("DB_USER")
	dbPassword := os.Getenv("DB_PASSWORD")
	dbName := os.Getenv("DB_NAME")
	dbHost := os.Getenv("DB_HOST")
	dbPort := os.Getenv("DB_PORT")

	db, err := sql.NewDB(dbUser, dbPassword, dbName, dbHost, dbPort)

	if err != nil {
		log.Fatalln(err)
	}

	defer sql.Close(db)

	err = http.NewRouter(db).Run(":" + os.Getenv("PORT"))

	if err != nil {
		log.Fatalln(err)
	}
}
