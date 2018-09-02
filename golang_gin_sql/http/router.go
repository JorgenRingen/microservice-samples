package http

import (
	"github.com/di0nys1us/microservice-samples/golang_gin_sql/api"
	"github.com/di0nys1us/microservice-samples/golang_gin_sql/service"
	"github.com/di0nys1us/microservice-samples/golang_gin_sql/sql"
	"github.com/gin-gonic/gin"
)

func NewRouter(db *sql.DB) *gin.Engine {
	router := gin.Default()
	router.RedirectTrailingSlash = false

	var companyService api.CompanyService = service.Company(db)
	var employeeService api.EmployeeService = service.Employee(db)

	companies := router.Group("/companies")
	// Create new company
	companies.POST("", PostCompanyHandler(companyService))
	// Find all companies
	companies.GET("", GetAllCompaniesHandler(companyService))

	company := companies.Group("/:companyID")
	// Find company by ID
	company.GET("", GetCompanyByIDHandler(companyService))
	// Delete company with ID
	company.DELETE("", DeleteCompanyWithIDHandler(companyService))

	companyEmployees := company.Group("/employees")
	// Add employee to company
	companyEmployees.POST("", PostEmployeeToCompanyHandler(companyService))

	companyEmployee := companyEmployees.Group("/:employeeID")
	// Remove employee from company
	companyEmployee.DELETE("", DeleteEmployeeFromCompanyHandler(companyService))

	employees := router.Group("/employees")
	// Create new employee
	employees.POST("", PostEmployeeHandler(employeeService))
	// Find all employees
	employees.GET("", GetAllEmployeesHandler(employeeService))

	employee := employees.Group("/:employeeID")
	// Find employee by ID
	employee.GET("", GetEmployeeByIDHandler(employeeService))
	// Update employee with ID
	employee.PUT("", PutEmployeeWithIDHandler(employeeService))
	// Delete employee with ID
	employee.DELETE("", DeleteEmployeeWithIDHandler(employeeService))

	return router
}
