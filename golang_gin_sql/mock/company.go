package mock

import (
	"log"
	"math/rand"
	"strconv"

	"github.com/di0nys1us/microservice-samples/golang_gin_sql/api"
)

type CompanyService struct {
	SaveCompanyFn                   func(company *api.Company) error
	SaveCompanyCalled               bool
	FindAllCompaniesFn              func() (*api.Companies, error)
	FindAllCompaniesCalled          bool
	FindCompanyByIDFn               func(companyID string) (*api.Company, error)
	FindCompanyByIDCalled           bool
	DeleteCompanyWithIDFn           func(companyID string) error
	DeleteCompanyWithIDCalled       bool
	AddEmployeeToCompanyFn          func(companyID string, employeeID string) error
	AddEmployeeToCompanyCalled      bool
	RemoveEmployeeFromCompanyFn     func(companyID string, employeeID string) error
	RemoveEmployeeFromCompanyCalled bool
}

func (m *CompanyService) SaveCompany(company *api.Company) error {
	log.Printf("[mock.CompanyService] SaveCompany(company = %+v)\n", company)

	m.SaveCompanyCalled = true

	if m.SaveCompanyFn == nil {
		return nil
	}

	return m.SaveCompanyFn(company)
}

func (m *CompanyService) FindAllCompanies() (*api.Companies, error) {
	log.Println("[mock.CompanyService] FindAllCompanies()")

	m.FindAllCompaniesCalled = true

	if m.FindAllCompaniesFn == nil {
		return nil, nil
	}

	return m.FindAllCompaniesFn()
}

func (m *CompanyService) FindCompanyByID(companyID string) (*api.Company, error) {
	log.Printf("[mock.CompanyService] FindCompanyByID(companyID = %s)\n", companyID)

	m.FindCompanyByIDCalled = true

	if m.FindCompanyByIDFn == nil {
		return nil, nil
	}

	return m.FindCompanyByIDFn(companyID)
}

func (m *CompanyService) DeleteCompanyWithID(companyID string) error {
	log.Printf("[mock.CompanyService] DeleteCompanyWithID(companyID = %s)\n", companyID)

	m.DeleteCompanyWithIDCalled = true

	if m.DeleteCompanyWithIDFn == nil {
		return nil
	}

	return m.DeleteCompanyWithIDFn(companyID)
}

func (m *CompanyService) AddEmployeeToCompany(companyID string, employeeID string) error {
	log.Printf("[mock.CompanyService] AddEmployeeToCompany(companyID = %s, employeeID = %s)\n", companyID, employeeID)

	m.AddEmployeeToCompanyCalled = true

	if m.AddEmployeeToCompanyFn == nil {
		return nil
	}

	return m.AddEmployeeToCompanyFn(companyID, employeeID)
}

func (m *CompanyService) RemoveEmployeeFromCompany(companyID string, employeeID string) error {
	log.Printf("[mock.CompanyService] RemoveEmployeeFromCompany(companyID = %s, employeeID = %s)\n", companyID, employeeID)

	m.RemoveEmployeeFromCompanyCalled = true

	if m.RemoveEmployeeFromCompanyFn == nil {
		return nil
	}

	return m.RemoveEmployeeFromCompanyFn(companyID, employeeID)
}

// DefaultCompanyService constructs a mocked CompanyService using some sensible defaults
func DefaultCompanyService() *CompanyService {
	return &CompanyService{
		SaveCompanyFn: func(company *api.Company) error {
			company.ID = strconv.Itoa(rand.Intn(100) + 1)
			return nil
		},
		FindAllCompaniesFn: func() (*api.Companies, error) {
			return &api.Companies{
				&api.Company{
					ID:   "1",
					Name: "Company A",
				},
				&api.Company{
					ID:   "2",
					Name: "Company B",
				},
				&api.Company{
					ID:   "3",
					Name: "Company C",
				},
			}, nil
		},
		FindCompanyByIDFn: func(companyID string) (*api.Company, error) {
			return nil, api.ErrNotFound
		},
		DeleteCompanyWithIDFn: func(companyID string) error {
			return nil
		},
		AddEmployeeToCompanyFn: func(companyID string, employeeID string) error {
			return nil
		},
		RemoveEmployeeFromCompanyFn: func(companyID string, employeeID string) error {
			return nil
		},
	}
}
