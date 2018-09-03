package http

import (
	"encoding/json"

	"github.com/di0nys1us/microservice-samples/golang_gin_sql/api"
	"github.com/gin-gonic/gin"
)

func bindCompany(c *gin.Context) (*api.Company, error) {
	var company struct {
		ID   json.Number `json:"id"`
		Name string      `json:"name,omitempty"`
	}

	err := c.ShouldBindJSON(&company)

	if err != nil {
		return nil, err
	}

	return &api.Company{
		ID:   company.ID.String(),
		Name: company.Name,
	}, nil
}

func postCompanyHandler(a api.CompanySaver) gin.HandlerFunc {
	return func(c *gin.Context) {
		created(c, func() (string, error) {
			company, err := bindCompany(c)

			if err != nil {
				return "", err
			}

			err = a.SaveCompany(company)

			if err != nil {
				return "", err
			}

			return "/companies/" + company.ID, nil
		})
	}
}

func getAllCompaniesHandler(a api.AllCompaniesFinder) gin.HandlerFunc {
	return func(c *gin.Context) {
		ok(c, func() (interface{}, error) {
			return a.FindAllCompanies()
		})
	}
}

func getCompanyByIDHandler(a api.CompanyByIDFinder) gin.HandlerFunc {
	return func(c *gin.Context) {
		ok(c, func() (interface{}, error) {
			return a.FindCompanyByID(c.Param("companyID"))
		})
	}
}

func deleteCompanyWithIDHandler(a api.CompanyWithIDDeleter) gin.HandlerFunc {
	return func(c *gin.Context) {
		noContent(c, func() error {
			return a.DeleteCompanyWithID(c.Param("companyID"))
		})
	}
}

func postEmployeeToCompanyHandler(a api.EmployeeToCompanyAdder) gin.HandlerFunc {
	return func(c *gin.Context) {
		noContent(c, func() error {
			var employeeID json.Number

			err := c.ShouldBindJSON(&employeeID)

			if err != nil {
				return err
			}

			return a.AddEmployeeToCompany(c.Param("companyID"), employeeID.String())
		})
	}
}

func deleteEmployeeFromCompanyHandler(a api.EmployeeFromCompanyRemover) gin.HandlerFunc {
	return func(c *gin.Context) {
		noContent(c, func() error {
			return a.RemoveEmployeeFromCompany(c.Param("companyID"), c.Param("employeeID"))
		})
	}
}
