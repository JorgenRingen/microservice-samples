package service

import (
	"sync"

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

func (s *companyService) FindCompanyByID(companyID string) (company *api.Company, err error) {
	companies := make(chan *api.Company, 1)
	employees := make(chan *api.Employees, 1)
	errs := make(chan error, 2)
	wg := sync.WaitGroup{}

	wg.Add(2)

	go func() {
		defer close(companies)
		defer wg.Done()

		c, err := sql.FindCompanyByID(s.db, companyID)

		if err != nil {
			errs <- err
			return
		}

		companies <- c
	}()

	go func() {
		defer close(employees)
		defer wg.Done()

		e, err := sql.FindCompanyEmployees(s.db, companyID)

		if err != nil {
			errs <- err
			return
		}

		employees <- e
	}()

	wg.Wait()

	close(errs)

	if len(errs) > 0 {
		return nil, <-errs
	}

	company = <-companies
	company.Employees = <-employees

	return company, nil
}

func (s *companyService) DeleteCompanyWithID(companyID string) error {
	return sql.DeleteCompanyWithID(s.db, companyID)
}

func (s *companyService) AddEmployeeToCompany(companyID string, employeeID string) (err error) {
	employee, err := sql.FindEmployeeByID(s.db, employeeID)

	if err != nil {
		return
	}

	if employee.CompanyID == companyID {
		return api.ErrDuplicate
	}

	return sql.AddEmployeeToCompany(s.db, companyID, employeeID)
}

func (s *companyService) RemoveEmployeeFromCompany(companyID string, employeeID string) (err error) {
	errs := make(chan error, 2)
	wg := sync.WaitGroup{}

	wg.Add(2)

	go func() {
		defer wg.Done()

		_, err := sql.FindCompanyByID(s.db, companyID)

		if err != nil {
			errs <- err
		}
	}()

	go func() {
		defer wg.Done()

		_, err := sql.FindEmployeeByID(s.db, employeeID)

		if err != nil {
			errs <- err
		}
	}()

	wg.Wait()

	close(errs)

	if len(errs) > 0 {
		return <-errs
	}

	err = sql.RemoveEmployeeFromCompany(s.db, companyID, employeeID)

	if err == api.ErrNotFound {
		return nil
	}

	return
}

// Company constructs a CompanyService
func Company(db *sql.DB) api.CompanyService {
	return &companyService{db}
}
