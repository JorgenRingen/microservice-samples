package sql

import (
	"database/sql"

	"github.com/di0nys1us/microservice-samples/golang_gin_sql/api"
)

// SaveEmployee saves an employee using the supplied RowQueryer
func SaveEmployee(dx RowQueryer, employee *api.Employee) error {
	const query = `
		insert into employee (date_of_birth, firstname, lastname)
		values ($1, $2, $3)
		returning id;
	`

	row := dx.QueryRow(
		query,
		employee.DateOfBirth,
		employee.FirstName,
		employee.LastName,
	)
	err := row.Scan(&employee.ID)

	if err != nil {
		return err
	}

	return nil
}

// UpdateEmployee updates an employee using the supplied Execer
func UpdateEmployee(dx Execer, employee *api.Employee) error {
	const query = `
		update employee set
			date_of_birth = $1,
			firstname = $2,
			lastname = $3
		where id = $4;
	`

	res, err := dx.Exec(
		query,
		employee.DateOfBirth,
		employee.FirstName,
		employee.LastName,
		employee.ID,
	)

	if err != nil {
		return err
	}

	return notFound(res)
}

// FindAllEmployees finds all employees using the supplied Queryer
func FindAllEmployees(dx Queryer) (*api.Employees, error) {
	const query = `
		select 
			employee.id,
			employee.date_of_birth,
			employee.firstname,
			employee.lastname,
			company_employees.company_id
		from employee
		left join company_employees on company_employees.employees_id = employee.id
		order by employee.id;
	`

	rows, err := dx.Query(query)

	if err != nil {
		return nil, err
	}

	defer Close(rows)

	employees := &api.Employees{}

	for rows.Next() {
		employee := &api.Employee{}
		companyID := sql.NullString{}
		err = rows.Scan(
			&employee.ID,
			&employee.DateOfBirth,
			&employee.FirstName,
			&employee.LastName,
			&companyID,
		)

		if err != nil {
			return nil, err
		}

		if companyID.Valid {
			employee.CompanyID = companyID.String
		}

		*employees = append(*employees, employee)
	}

	err = rows.Err()

	if err != nil {
		return nil, err
	}

	return employees, nil
}

// FindEmployeeByID finds an employee by employeeID using the supplied RowQueryer
func FindEmployeeByID(dx RowQueryer, employeeID string) (*api.Employee, error) {
	const query = `
		select
			employee.id,
			employee.date_of_birth,
			employee.firstname,
			employee.lastname,
			company_employees.company_id
		from employee
		left join company_employees on company_employees.employees_id = employee.id
		where employee.id = $1;
	`

	employee := &api.Employee{}
	companyID := sql.NullString{}
	row := dx.QueryRow(query, employeeID)
	err := row.Scan(
		&employee.ID,
		&employee.DateOfBirth,
		&employee.FirstName,
		&employee.LastName,
		&companyID,
	)

	if err == sql.ErrNoRows {
		return nil, api.ErrNotFound
	}

	if err != nil {
		return nil, err
	}

	if companyID.Valid {
		employee.CompanyID = companyID.String
	}

	return employee, nil
}

// DeleteEmployeeWithID deletes the employee with employeeID using the supplied Execer
func DeleteEmployeeWithID(dx Execer, employeeID string) error {
	const query = `
		delete from employee
		where id = $1;
	`

	res, err := dx.Exec(query, employeeID)

	if err != nil {
		return err
	}

	return notFound(res)
}
