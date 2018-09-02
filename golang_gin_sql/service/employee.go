package service

import (
	"github.com/di0nys1us/microservice-samples/golang_gin_sql/api"
	"github.com/di0nys1us/microservice-samples/golang_gin_sql/sql"
)

type employeeService struct {
	db *sql.DB
}

func (s *employeeService) SaveEmployee(employee *api.Employee) error {
	if employee.ID == "" {
		return sql.SaveEmployee(s.db, employee)
	}

	return sql.UpdateEmployee(s.db, employee)
}

func (s *employeeService) FindAllEmployees() (*api.Employees, error) {
	return sql.FindAllEmployees(s.db)
}

func (s *employeeService) FindEmployeeByID(employeeID string) (*api.Employee, error) {
	return sql.FindEmployeeByID(s.db, employeeID)
}

func (s *employeeService) DeleteEmployeeWithID(employeeID string) error {
	return sql.DeleteEmployeeWithID(s.db, employeeID)
}

func Employee(db *sql.DB) *employeeService {
	return &employeeService{db}
}
