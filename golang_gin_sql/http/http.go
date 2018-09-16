package http

import (
	"fmt"
	"net/http"

	"github.com/di0nys1us/microservice-samples/golang_gin_sql/api"
	"github.com/gin-gonic/gin"
)

type objFunc func() (interface{}, error)

type errFunc func() error

type locFunc func() (string, error)

func ok(c *gin.Context, fn objFunc) {
	obj, err := fn()

	if code, ok := err.(api.HttpStatusCode); ok {
		c.Status(int(code))
		return
	}

	if err != nil {
		c.Error(err)
		return
	}

	c.JSON(http.StatusOK, obj)
}

func created(c *gin.Context, fn locFunc) {
	loc, err := fn()

	if code, ok := err.(api.HttpStatusCode); ok {
		c.Status(int(code))
		return
	}

	if err != nil {
		c.Error(err)
		return
	}

	setLocation(c, loc)

	c.Status(http.StatusCreated)
}

func noContent(c *gin.Context, fn errFunc) {
	err := fn()

	if code, ok := err.(api.HttpStatusCode); ok {
		c.Status(int(code))
		return
	}

	if err != nil {
		c.Error(err)
		return
	}

	c.Status(http.StatusNoContent)
}

func setLocation(c *gin.Context, loc string) {
	scheme := "http"

	if c.Request.TLS != nil {
		scheme = "https"
	}

	c.Header("Location", fmt.Sprintf("%s://%s%s", scheme, c.Request.Host, loc))
}
