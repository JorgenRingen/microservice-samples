package api

import (
	"net/http"
)

var (
	ErrNotFound  = HttpStatusCode(http.StatusNotFound)
	ErrDuplicate = HttpStatusCode(http.StatusBadRequest)
)

type HttpStatusCode int

func (c HttpStatusCode) Error() string {
	return http.StatusText(int(c))
}
