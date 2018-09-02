package sql

import (
	"database/sql"
	"fmt"
	"io"
	"log"
)

type Queryer interface {
	Query(query string, args ...interface{}) (*sql.Rows, error)
}

type RowQueryer interface {
	QueryRow(query string, args ...interface{}) *sql.Row
}

type Execer interface {
	Exec(query string, args ...interface{}) (sql.Result, error)
}

func Close(c io.Closer) {
	err := c.Close()

	if err != nil {
		log.Println(err)
	}
}

type DB struct {
	*sql.DB
}

func NewDB(user, password, database, host, port string) (*DB, error) {
	const format = "user=%s password=%s dbname=%s host=%s port=%s sslmode=disable options='-c search_path=public'"

	db, err := sql.Open("postgres", fmt.Sprintf(format, user, password, database, host, port))

	if err != nil {
		return nil, err
	}

	return &DB{db}, nil
}
