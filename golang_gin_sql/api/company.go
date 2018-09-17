package api

type Company struct {
	ID        string     `json:"id"`
	Name      string     `json:"name,omitempty"`
	Employees *Employees `json:"employees,omitempty"`
}

type Companies []*Company

type CompanySaver interface {
	SaveCompany(company *Company) error
}

type AllCompaniesFinder interface {
	FindAllCompanies() (*Companies, error)
}

type CompanyByIDFinder interface {
	FindCompanyByID(companyID string) (*Company, error)
}

type CompanyWithIDDeleter interface {
	DeleteCompanyWithID(companyID string) error
}

type EmployeeToCompanyAdder interface {
	AddEmployeeToCompany(companyID, employeeID string) error
}

type EmployeeFromCompanyRemover interface {
	RemoveEmployeeFromCompany(companyID, employeeID string) error
}

type CompanyService interface {
	CompanySaver
	AllCompaniesFinder
	CompanyByIDFinder
	CompanyWithIDDeleter
	EmployeeToCompanyAdder
	EmployeeFromCompanyRemover
}
