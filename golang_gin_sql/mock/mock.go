package mock

import (
	"github.com/di0nys1us/microservice-samples/golang_gin_sql/api"
	"github.com/stretchr/testify/mock"
)

func SetID(args mock.Arguments) {
	if company, ok := args.Get(0).(*api.Company); ok {
		company.ID = "1"
		return
	}

	if employee, ok := args.Get(0).(*api.Employee); ok {
		employee.ID = "1"
		return
	}
}
