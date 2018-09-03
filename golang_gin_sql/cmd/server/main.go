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

	db, err := sql.NewDB(
		os.Getenv("DB_USER"),
		os.Getenv("DB_PASSWORD"),
		os.Getenv("DB_NAME"),
		os.Getenv("DB_HOST"),
		os.Getenv("DB_PORT"),
	)

	if err != nil {
		log.Fatalln(err)
	}

	defer sql.Close(db)

	err = http.NewRouter(db).Run(":" + os.Getenv("PORT"))

	if err != nil {
		log.Fatalln(err)
	}
}
