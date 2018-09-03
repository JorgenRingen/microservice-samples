package sql

import (
	"database/sql"

	"github.com/di0nys1us/microservice-samples/golang_gin_sql/api"
)

// SaveCompany saves a company using the supplied RowQueryer
func SaveCompany(dx RowQueryer, company *api.Company) error {
	const query = `
		insert into company (name)
		values ($1)
		returning id;
	`

	row := dx.QueryRow(query, company.Name)
	err := row.Scan(&company.ID)

	if err != nil {
		return err
	}

	return nil
}

// FindAllCompanies finds all companies using the supplied Queryer
func FindAllCompanies(dx Queryer) (*api.Companies, error) {
	const query = `
		select
			id,
			name
		from company
		order by id;
	`

	rows, err := dx.Query(query)

	if err != nil {
		return nil, err
	}

	defer Close(rows)

	companies := &api.Companies{}

	for rows.Next() {
		company := &api.Company{}
		err = rows.Scan(&company.ID, &company.Name)

		if err != nil {
			return nil, err
		}

		*companies = append(*companies, company)
	}

	err = rows.Err()

	if err != nil {
		return nil, err
	}

	return companies, nil
}

// FindCompanyByID finds a company by companyID using the supplied RowQueryer
func FindCompanyByID(dx RowQueryer, companyID string) (*api.Company, error) {
	const query = `
		select
			id,
			name
		from company
		where id = $1;
	`

	company := &api.Company{}
	row := dx.QueryRow(query, companyID)
	err := row.Scan(&company.ID, &company.Name)

	if err == sql.ErrNoRows {
		return nil, api.ErrNotFound
	}

	if err != nil {
		return nil, err
	}

	return company, nil
}

// FindCompanyEmployees finds the employees of a company using the supplied Queryer
func FindCompanyEmployees(dx Queryer, companyID string) (*api.Employees, error) {
	const query = `
		select
			employee.id,
			employee.date_of_birth,
			employee.firstname,
			employee.lastname
		from employee
		inner join company_employees on company_employees.employees_id = employee.id
		where company_employees.company_id = $1
		order by employee.id;
	`

	rows, err := dx.Query(query, companyID)

	if err != nil {
		return nil, err
	}

	defer Close(rows)

	employees := &api.Employees{}

	for rows.Next() {
		employee := &api.Employee{}
		err = rows.Scan(
			&employee.ID,
			&employee.DateOfBirth,
			&employee.FirstName,
			&employee.LastName,
		)

		if err != nil {
			return nil, err
		}

		employee.CompanyID = companyID
		*employees = append(*employees, employee)
	}

	err = rows.Err()

	if err != nil {
		return nil, err
	}

	return employees, nil
}

// DeleteCompanyWithID deletes the company with companyID using the supplied Execer
func DeleteCompanyWithID(dx Execer, companyID string) error {
	const query = `
		delete from company
		where id = $1; 
	`

	res, err := dx.Exec(query, companyID)

	if err != nil {
		return err
	}

	return notFound(res)
}

// AddEmployeeToCompany adds an employee to a company using the supplied Execer
func AddEmployeeToCompany(dx Execer, companyID string, employeeID string) error {
	const query = `
		insert into company_employees (company_id, employees_id)
		select
			company.id,
			employee.id
		from company, employee
		where 1 = 1
			and company.id = $1
			and employee.id = $2;
	`

	res, err := dx.Exec(query, companyID, employeeID)

	if err != nil {
		return err
	}

	return notFound(res)
}

// RemoveEmployeeFromCompany removes an employee from a company using the supplied Execer
func RemoveEmployeeFromCompany(dx Execer, companyID string, employeeID string) error {
	const query = `
		delete from company_employees
		where company_id = $1 and employees_id = $2;
	`

	res, err := dx.Exec(query, companyID, employeeID)

	if err != nil {
		return err
	}

	return notFound(res)
}
