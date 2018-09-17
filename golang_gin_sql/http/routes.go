package http

import (
	"log"
	"net/http"

	"github.com/di0nys1us/microservice-samples/golang_gin_sql/api"

	"github.com/gin-gonic/gin"
)

// Configure all the API routes for the application
func Configure(router *gin.Engine, companyService api.CompanyService, employeeService api.EmployeeService) {
	router.RedirectTrailingSlash = false
	router.Use(errorHandler())

	configureCompanyRoutes(router, companyService)
	configureEmployeeRoutes(router, employeeService)
}

func errorHandler() gin.HandlerFunc {
	return func(c *gin.Context) {
		c.Next()

		if len(c.Errors) > 0 {
			c.Status(http.StatusInternalServerError)
		}

		if gin.Mode() != gin.TestMode {
			for _, err := range c.Errors {
				log.Println(err)
			}
		}
	}
}

func configureCompanyRoutes(router *gin.Engine, companyService api.CompanyService) {
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
}

func configureEmployeeRoutes(router *gin.Engine, employeeService api.EmployeeService) {
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
}
