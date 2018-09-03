package mock

import (
	"log"
	"math/rand"
	"strconv"
	"time"

	"github.com/di0nys1us/microservice-samples/golang_gin_sql/api"
)

type EmployeeService struct {
	SaveEmployeeFn             func(employee *api.Employee) error
	SaveEmployeeCalled         bool
	FindAllEmployeesFn         func() (*api.Employees, error)
	FindAllEmployeesCalled     bool
	FindEmployeeByIDFn         func(employeeID string) (*api.Employee, error)
	FindEmployeeByIDCalled     bool
	DeleteEmployeeWithIDFn     func(employeeID string) error
	DeleteEmployeeWithIDCalled bool
}

func (m *EmployeeService) SaveEmployee(employee *api.Employee) error {
	log.Printf("[mock.EmployeeService] SaveEmployee(employee = %+v)\n", employee)

	m.SaveEmployeeCalled = true

	if m.SaveEmployeeFn == nil {
		return nil
	}

	return m.SaveEmployeeFn(employee)
}

func (m *EmployeeService) FindAllEmployees() (*api.Employees, error) {
	log.Println("[mock.EmployeeService] FindAllEmployees()")

	m.FindAllEmployeesCalled = true

	if m.FindAllEmployeesFn == nil {
		return nil, nil
	}

	return m.FindAllEmployeesFn()
}

func (m *EmployeeService) FindEmployeeByID(employeeID string) (*api.Employee, error) {
	log.Printf("[mock.EmployeeService] FindEmployeeByID(employeeID = %s)\n", employeeID)

	m.FindEmployeeByIDCalled = true

	if m.FindEmployeeByIDFn == nil {
		return nil, nil
	}

	return m.FindEmployeeByIDFn(employeeID)
}

func (m *EmployeeService) DeleteEmployeeWithID(employeeID string) error {
	log.Printf("[mock.EmployeeService] DeleteEmployeeWithID(employeeID = %s)\n", employeeID)

	m.DeleteEmployeeWithIDCalled = true

	if m.DeleteEmployeeWithIDFn == nil {
		return nil
	}

	return m.DeleteEmployeeWithIDFn(employeeID)
}

// DefaultEmployeeService constructs a mocked EmployeeService using some sensible defaults
func DefaultEmployeeService() *EmployeeService {
	return &EmployeeService{
		SaveEmployeeFn: func(employee *api.Employee) error {
			employee.ID = strconv.Itoa(rand.Intn(100) + 1)
			return nil
		},
		FindAllEmployeesFn: func() (*api.Employees, error) {
			return &api.Employees{
				&api.Employee{
					ID:          "1",
					DateOfBirth: &api.Date{Time: time.Now()},
					FirstName:   "First",
					LastName:    "Last",
				},
			}, nil
		},
		FindEmployeeByIDFn: func(employeeID string) (*api.Employee, error) {
			return &api.Employee{
				ID:          employeeID,
				DateOfBirth: &api.Date{Time: time.Now()},
				FirstName:   "First",
				LastName:    "Last",
			}, nil
		},
	}
}
