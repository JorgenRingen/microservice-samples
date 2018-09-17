package sql

import (
	"database/sql"
	"fmt"
	"io"
	"log"

	"github.com/di0nys1us/microservice-samples/golang_gin_sql/api"
)

// A Queryer queries for multiple rows
type Queryer interface {
	Query(query string, args ...interface{}) (*sql.Rows, error)
}

// A RowQueryer queries for a single row
type RowQueryer interface {
	QueryRow(query string, args ...interface{}) *sql.Row
}

// An Execer executes a query
type Execer interface {
	Exec(query string, args ...interface{}) (sql.Result, error)
}

// Close closes the supplied io.Closer and logs the output if an error occurs
func Close(c io.Closer) {
	err := c.Close()

	if err != nil {
		log.Println(err)
	}
}

// DB is a wrapper for sql.DB
type DB struct {
	*sql.DB
}

// NewDB constructs a new DB using the supplied parameters
func NewDB(user, password, database, host, port string) (*DB, error) {
	const format = "user=%s password=%s dbname=%s host=%s port=%s sslmode=disable options='-c search_path=public'"

	db, err := sql.Open("postgres", fmt.Sprintf(format, user, password, database, host, port))

	if err != nil {
		return nil, err
	}

	return &DB{db}, nil
}

func notFound(res sql.Result) (err error) {
	rowsAffected, err := res.RowsAffected()

	if err != nil {
		return
	}

	if rowsAffected == 0 {
		err = api.ErrNotFound
	}

	return
}
