package mock

import (
	"github.com/di0nys1us/microservice-samples/golang_gin_sql/api"
	"github.com/stretchr/testify/mock"
)

type EmployeeService struct {
	mock.Mock
}

func (m *EmployeeService) SaveEmployee(employee *api.Employee) error {
	args := m.Called(employee)
	return args.Error(0)
}

func (m *EmployeeService) FindAllEmployees() (*api.Employees, error) {
	args := m.Called()
	obj := args.Get(0)

	if obj == nil {
		return nil, args.Error(1)
	}

	return obj.(*api.Employees), args.Error(1)
}

func (m *EmployeeService) FindEmployeeByID(employeeID string) (*api.Employee, error) {
	args := m.Called(employeeID)
	obj := args.Get(0)

	if obj == nil {
		return nil, args.Error(1)
	}

	return obj.(*api.Employee), args.Error(1)
}

func (m *EmployeeService) DeleteEmployeeWithID(employeeID string) error {
	args := m.Called(employeeID)
	return args.Error(0)
}

// DefaultEmployeeService constructs a mocked EmployeeService using some sensible defaults
func DefaultEmployeeService() *EmployeeService {
	return new(EmployeeService)
}
