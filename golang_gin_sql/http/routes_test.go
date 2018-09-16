package http

import (
	"errors"
	"fmt"
	"net/http"
	"net/http/httptest"
	"strings"
	"testing"
	"time"

	"github.com/stretchr/testify/assert"

	"github.com/di0nys1us/microservice-samples/golang_gin_sql/api"
	"github.com/di0nys1us/microservice-samples/golang_gin_sql/mock"
	"github.com/gin-gonic/gin"
)

func TestRoutes(t *testing.T) {
	t.Run("CompanyRoutes", func(t *testing.T) {
		var tests = []struct {
			method     string
			target     string
			body       string
			statusCode int
			location   string
			configure  func(mock *mock.CompanyService)
		}{
			{
				method:     http.MethodPost,
				target:     "/companies",
				body:       `{ "name": "test" }`,
				statusCode: http.StatusCreated,
				location:   "http://example.com/companies/1",
				configure: func(m *mock.CompanyService) {
					m.On("SaveCompany", &api.Company{Name: "test"}).Run(mock.SetID).Return(nil)
				},
			},
			{
				method:     http.MethodPost,
				target:     "/companies",
				body:       `{ "name": "test" }`,
				statusCode: http.StatusInternalServerError,
				configure: func(m *mock.CompanyService) {
					m.On("SaveCompany", &api.Company{Name: "test"}).Return(errors.New("failed"))
				},
			},
			{
				method:     http.MethodGet,
				target:     "/companies",
				statusCode: http.StatusOK,
				configure: func(m *mock.CompanyService) {
					m.On("FindAllCompanies").Return(nil, nil)
				},
			},
			{
				method:     http.MethodGet,
				target:     "/companies",
				statusCode: http.StatusInternalServerError,
				configure: func(m *mock.CompanyService) {
					m.On("FindAllCompanies").Return(nil, errors.New("failed"))
				},
			},
			{
				method:     http.MethodGet,
				target:     "/companies/1",
				statusCode: http.StatusOK,
				configure: func(m *mock.CompanyService) {
					m.On("FindCompanyByID", "1").Return(nil, nil)
				},
			},
			{
				method:     http.MethodGet,
				target:     "/companies/-1",
				statusCode: http.StatusNotFound,
				configure: func(m *mock.CompanyService) {
					m.On("FindCompanyByID", "-1").Return(nil, api.ErrNotFound)
				},
			},
			{
				method:     http.MethodGet,
				target:     "/companies/1",
				statusCode: http.StatusInternalServerError,
				configure: func(m *mock.CompanyService) {
					m.On("FindCompanyByID", "1").Return(nil, errors.New("failed"))
				},
			},
			{
				method:     http.MethodDelete,
				target:     "/companies/1",
				statusCode: http.StatusNoContent,
				configure: func(m *mock.CompanyService) {
					m.On("DeleteCompanyWithID", "1").Return(nil)
				},
			},
			{
				method:     http.MethodDelete,
				target:     "/companies/1",
				statusCode: http.StatusInternalServerError,
				configure: func(m *mock.CompanyService) {
					m.On("DeleteCompanyWithID", "1").Return(errors.New("failed"))
				},
			},
			{
				method:     http.MethodPost,
				target:     "/companies/1/employees",
				body:       "2",
				statusCode: http.StatusNoContent,
				configure: func(m *mock.CompanyService) {
					m.On("AddEmployeeToCompany", "1", "2").Return(nil)
				},
			},
			{
				method:     http.MethodPost,
				target:     "/companies/1/employees",
				body:       "2",
				statusCode: http.StatusNotFound,
				configure: func(m *mock.CompanyService) {
					m.On("AddEmployeeToCompany", "1", "2").Return(api.ErrNotFound)
				},
			},
			{
				method:     http.MethodPost,
				target:     "/companies/1/employees",
				body:       "2",
				statusCode: http.StatusBadRequest,
				configure: func(m *mock.CompanyService) {
					m.On("AddEmployeeToCompany", "1", "2").Return(api.ErrDuplicate)
				},
			},
			{
				method:     http.MethodPost,
				target:     "/companies/1/employees",
				body:       "2",
				statusCode: http.StatusInternalServerError,
				configure: func(m *mock.CompanyService) {
					m.On("AddEmployeeToCompany", "1", "2").Return(errors.New("failed"))
				},
			},
			{
				method:     http.MethodDelete,
				target:     "/companies/1/employees/2",
				statusCode: http.StatusNoContent,
				configure: func(m *mock.CompanyService) {
					m.On("RemoveEmployeeFromCompany", "1", "2").Return(nil)
				},
			},
			{
				method:     http.MethodDelete,
				target:     "/companies/1/employees/2",
				statusCode: http.StatusInternalServerError,
				configure: func(m *mock.CompanyService) {
					m.On("RemoveEmployeeFromCompany", "1", "2").Return(errors.New("failed"))
				},
			},
		}

		for _, test := range tests {
			t.Run(fmt.Sprintf("%s %s should return %d", test.method, test.target, test.statusCode), func(t *testing.T) {
				gin.SetMode(gin.TestMode)

				router := gin.New()
				mockCompanyService := mock.DefaultCompanyService()

				Configure(router, mockCompanyService, nil)

				test.configure(mockCompanyService)

				w := httptest.NewRecorder()
				r := httptest.NewRequest(test.method, test.target, strings.NewReader(test.body))

				router.ServeHTTP(w, r)

				res := w.Result()

				assert.Equal(t, test.statusCode, res.StatusCode)
				assert.Equal(t, test.location, res.Header.Get("Location"))
			})
		}
	})

	t.Run("EmployeeRoutes", func(t *testing.T) {
		var date = api.NewDate(1980, time.December, 20)
		var tests = []struct {
			method     string
			target     string
			body       string
			statusCode int
			location   string
			configure  func(m *mock.EmployeeService)
		}{
			{
				method:     http.MethodPost,
				target:     "/employees",
				body:       `{ "dateOfBirth": [1980, 12, 20] }`,
				statusCode: http.StatusCreated,
				location:   "http://example.com/employees/1",
				configure: func(m *mock.EmployeeService) {
					m.On("SaveEmployee", &api.Employee{DateOfBirth: &date}).Run(mock.SetID).Return(nil)
				},
			},
			{
				method:     http.MethodPost,
				target:     "/employees",
				body:       `{ "dateOfBirth": [1980, 12, 20] }`,
				statusCode: http.StatusInternalServerError,
				configure: func(m *mock.EmployeeService) {
					m.On("SaveEmployee", &api.Employee{DateOfBirth: &date}).Return(errors.New("failed"))
				},
			},
			{
				method:     http.MethodGet,
				target:     "/employees",
				statusCode: http.StatusOK,
				configure: func(m *mock.EmployeeService) {
					m.On("FindAllEmployees").Return(nil, nil)
				},
			},
			{
				method:     http.MethodGet,
				target:     "/employees",
				statusCode: http.StatusInternalServerError,
				configure: func(m *mock.EmployeeService) {
					m.On("FindAllEmployees").Return(nil, errors.New("failed"))
				},
			},
			{
				method:     http.MethodGet,
				target:     "/employees/1",
				statusCode: http.StatusOK,
				configure: func(m *mock.EmployeeService) {
					m.On("FindEmployeeByID", "1").Return(nil, nil)
				},
			},
			{
				method:     http.MethodGet,
				target:     "/employees/-1",
				statusCode: http.StatusNotFound,
				configure: func(m *mock.EmployeeService) {
					m.On("FindEmployeeByID", "-1").Return(nil, api.ErrNotFound)
				},
			},
			{
				method:     http.MethodGet,
				target:     "/employees/1",
				statusCode: http.StatusInternalServerError,
				configure: func(m *mock.EmployeeService) {
					m.On("FindEmployeeByID", "1").Return(nil, errors.New("failed"))
				},
			},
			{
				method:     http.MethodPut,
				target:     "/employees/1",
				body:       `{ "id": "1", "dateOfBirth": [1980, 12, 20] }`,
				statusCode: http.StatusNoContent,
				configure: func(m *mock.EmployeeService) {
					m.On("SaveEmployee", &api.Employee{ID: "1", DateOfBirth: &date}).Return(nil)
				},
			},
			{
				method:     http.MethodPut,
				target:     "/employees/1",
				body:       `{ "id": "2", "dateOfBirth": [1980, 12, 20] }`,
				statusCode: http.StatusBadRequest,
				configure:  func(m *mock.EmployeeService) {},
			},
			{
				method:     http.MethodPut,
				target:     "/employees/1",
				body:       `{ "id": "1", "dateOfBirth": [1980, 12, 20] }`,
				statusCode: http.StatusInternalServerError,
				configure: func(m *mock.EmployeeService) {
					m.On("SaveEmployee", &api.Employee{ID: "1", DateOfBirth: &date}).Return(errors.New("failed"))
				},
			},
			{
				method:     http.MethodDelete,
				target:     "/employees/1",
				statusCode: http.StatusNoContent,
				configure: func(m *mock.EmployeeService) {
					m.On("DeleteEmployeeWithID", "1").Return(nil)
				},
			},
			{
				method:     http.MethodDelete,
				target:     "/employees/1",
				statusCode: http.StatusInternalServerError,
				configure: func(m *mock.EmployeeService) {
					m.On("DeleteEmployeeWithID", "1").Return(errors.New("failed"))
				},
			},
		}

		for _, test := range tests {
			t.Run(fmt.Sprintf("%s %s should return %d", test.method, test.target, test.statusCode), func(t *testing.T) {
				gin.SetMode(gin.TestMode)

				router := gin.New()
				mockEmployeeService := mock.DefaultEmployeeService()

				Configure(router, nil, mockEmployeeService)

				test.configure(mockEmployeeService)

				w := httptest.NewRecorder()
				r := httptest.NewRequest(test.method, test.target, strings.NewReader(test.body))

				router.ServeHTTP(w, r)

				res := w.Result()

				assert.Equal(t, test.statusCode, res.StatusCode)
				assert.Equal(t, test.location, res.Header.Get("Location"))
			})
		}
	})
}
