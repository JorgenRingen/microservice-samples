package service

import (
	"github.com/di0nys1us/microservice-samples/golang_gin_sql/api"
	"github.com/di0nys1us/microservice-samples/golang_gin_sql/sql"
)

type companyService struct {
	db *sql.DB
}

func (s *companyService) SaveCompany(company *api.Company) error {
	return sql.SaveCompany(s.db, company)
}

func (s *companyService) FindAllCompanies() (*api.Companies, error) {
	return sql.FindAllCompanies(s.db)
}

func (s *companyService) FindCompanyByID(companyID string) (*api.Company, error) {
	company, err := sql.FindCompanyByID(s.db, companyID)

	if err != nil {
		return nil, err
	}

	employees, err := sql.FindCompanyEmployees(s.db, companyID)

	if err != nil {
		return nil, err
	}

	company.Employees = employees

	return company, nil
}

func (s *companyService) DeleteCompanyWithID(companyID string) error {
	return sql.DeleteCompanyWithID(s.db, companyID)
}

func (s *companyService) AddEmployeeToCompany(companyID string, employeeID string) error {
	employee, err := sql.FindEmployeeByID(s.db, employeeID)

	if err != nil {
		return err
	}

	if employee.CompanyID == companyID {
		return api.ErrDuplicate
	}

	return sql.AddEmployeeToCompany(s.db, companyID, employeeID)
}

func (s *companyService) RemoveEmployeeFromCompany(companyID string, employeeID string) error {
	_, err := sql.FindCompanyByID(s.db, companyID)

	if err != nil {
		return err
	}

	_, err = sql.FindEmployeeByID(s.db, employeeID)

	if err != nil {
		return err
	}

	err = sql.RemoveEmployeeFromCompany(s.db, companyID, employeeID)

	if err == api.ErrNotFound {
		return nil
	}

	return err
}

func Company(db *sql.DB) *companyService {
	return &companyService{db}
}
