package http

import (
	"github.com/di0nys1us/microservice-samples/golang_gin_sql/service"
	"github.com/di0nys1us/microservice-samples/golang_gin_sql/sql"

	"github.com/gin-gonic/gin"
)

// NewRouter constructs a default Gin router with all routes configured
func NewRouter(db *sql.DB) *gin.Engine {
	router := gin.Default()
	router.RedirectTrailingSlash = false

	companyService := service.Company(db)
	employeeService := service.Employee(db)

	companies := router.Group("/companies")
	// Create new company
	companies.POST("", postCompanyHandler(companyService))
	// Find all companies
	companies.GET("", getAllCompaniesHandler(companyService))

	company := companies.Group("/:companyID")
	// Find company by ID
	company.GET("", getCompanyByIDHandler(companyService))
	// Delete company with ID
	company.DELETE("", deleteCompanyWithIDHandler(companyService))

	companyEmployees := company.Group("/employees")
	// Add employee to company
	companyEmployees.POST("", postEmployeeToCompanyHandler(companyService))

	companyEmployee := companyEmployees.Group("/:employeeID")
	// Remove employee from company
	companyEmployee.DELETE("", deleteEmployeeFromCompanyHandler(companyService))

	employees := router.Group("/employees")
	// Create new employee
	employees.POST("", postEmployeeHandler(employeeService))
	// Find all employees
	employees.GET("", getAllEmployeesHandler(employeeService))

	employee := employees.Group("/:employeeID")
	// Find employee by ID
	employee.GET("", getEmployeeByIDHandler(employeeService))
	// Update employee with ID
	employee.PUT("", putEmployeeWithIDHandler(employeeService))
	// Delete employee with ID
	employee.DELETE("", deleteEmployeeWithIDHandler(employeeService))

	return router
}
