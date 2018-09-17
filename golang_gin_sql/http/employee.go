package http

import (
	"encoding/json"
	"net/http"

	"github.com/di0nys1us/microservice-samples/golang_gin_sql/api"
	"github.com/gin-gonic/gin"
)

func bindEmployee(c *gin.Context) (*api.Employee, error) {
	var employee struct {
		ID          json.Number `json:"id"`
		DateOfBirth *api.Date   `json:"dateOfBirth,omitempty"`
		FirstName   string      `json:"firstname,omitempty"`
		LastName    string      `json:"lastname,omitempty"`
	}

	err := c.ShouldBindJSON(&employee)

	if err != nil {
		return nil, err
	}

	return &api.Employee{
		ID:          employee.ID.String(),
		DateOfBirth: employee.DateOfBirth,
		FirstName:   employee.FirstName,
		LastName:    employee.LastName,
	}, nil
}

func postEmployeeHandler(a api.EmployeeSaver) gin.HandlerFunc {
	return func(c *gin.Context) {
		created(c, func() (string, error) {
			employee, err := bindEmployee(c)

			if err != nil {
				return "", err
			}

			err = a.SaveEmployee(employee)

			if err != nil {
				return "", err
			}

			return "/employees/" + employee.ID, nil
		})
	}
}

func getAllEmployeesHandler(a api.AllEmployeesFinder) gin.HandlerFunc {
	return func(c *gin.Context) {
		ok(c, func() (interface{}, error) {
			return a.FindAllEmployees()
		})
	}
}

func getEmployeeByIDHandler(a api.EmployeeByIDFinder) gin.HandlerFunc {
	return func(c *gin.Context) {
		ok(c, func() (interface{}, error) {
			return a.FindEmployeeByID(c.Param("employeeID"))
		})
	}
}

func putEmployeeWithIDHandler(a api.EmployeeSaver) gin.HandlerFunc {
	return func(c *gin.Context) {
		noContent(c, func() error {
			employee, err := bindEmployee(c)

			if err != nil {
				return err
			}

			if employee.ID != c.Param("employeeID") {
				return api.HttpStatusCode(http.StatusBadRequest)
			}

			return a.SaveEmployee(employee)
		})
	}
}

func deleteEmployeeWithIDHandler(a api.EmployeeWithIDDeleter) gin.HandlerFunc {
	return func(c *gin.Context) {
		noContent(c, func() error {
			return a.DeleteEmployeeWithID(c.Param("employeeID"))
		})
	}
}
