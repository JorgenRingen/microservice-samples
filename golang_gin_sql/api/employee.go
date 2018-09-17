package api

type Employee struct {
	ID          string `json:"id"`
	DateOfBirth *Date  `json:"dateOfBirth,omitempty"`
	FirstName   string `json:"firstname,omitempty"`
	LastName    string `json:"lastname,omitempty"`
	CompanyID   string `json:"-"`
}

type Employees []*Employee

type EmployeeSaver interface {
	SaveEmployee(employee *Employee) error
}

type AllEmployeesFinder interface {
	FindAllEmployees() (*Employees, error)
}

type EmployeeByIDFinder interface {
	FindEmployeeByID(employeeID string) (*Employee, error)
}

type EmployeeWithIDDeleter interface {
	DeleteEmployeeWithID(employeeID string) error
}

type EmployeeService interface {
	EmployeeSaver
	AllEmployeesFinder
	EmployeeByIDFinder
	EmployeeWithIDDeleter
}
