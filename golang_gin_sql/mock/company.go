package mock

import (
	"github.com/di0nys1us/microservice-samples/golang_gin_sql/api"
	"github.com/stretchr/testify/mock"
)

type CompanyService struct {
	mock.Mock
}

func (m *CompanyService) SaveCompany(company *api.Company) error {
	args := m.Called(company)
	return args.Error(0)
}

func (m *CompanyService) FindAllCompanies() (*api.Companies, error) {
	args := m.Called()
	obj := args.Get(0)

	if obj == nil {
		return nil, args.Error(1)
	}

	return obj.(*api.Companies), args.Error(1)
}

func (m *CompanyService) FindCompanyByID(companyID string) (*api.Company, error) {
	args := m.Called(companyID)
	obj := args.Get(0)

	if obj == nil {
		return nil, args.Error(1)
	}

	return obj.(*api.Company), args.Error(1)
}

func (m *CompanyService) DeleteCompanyWithID(companyID string) error {
	args := m.Called(companyID)
	return args.Error(0)
}

func (m *CompanyService) AddEmployeeToCompany(companyID string, employeeID string) error {
	args := m.Called(companyID, employeeID)
	return args.Error(0)
}

func (m *CompanyService) RemoveEmployeeFromCompany(companyID string, employeeID string) error {
	args := m.Called(companyID, employeeID)
	return args.Error(0)
}

// DefaultCompanyService constructs a mocked CompanyService using some sensible defaults
func DefaultCompanyService() *CompanyService {
	return &CompanyService{}
}
