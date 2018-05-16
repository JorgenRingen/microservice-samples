package org.mssamples.tests;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mssamples.tests.model.Company;
import org.mssamples.tests.model.Employee;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class CompaniesTest {

    private RestClient restClient = new RestClient();

    @Test
    void findAllCompaniesShouldReturn200() {
        List<Company> companies = restClient.findAllCompanies();
        assertThat(companies).isNotNull();
    }

    @Test
    void findAllShouldReturnCompanyThatExists() {
        var name = "ACME Industries";
        Company company = new Company();
        company.setName(name);

        ResponseEntity<Company> createCompanyResponse = restClient.createCompany(company);
        assertThat(createCompanyResponse.getStatusCode()).as("Status code from create company should be 201").isEqualTo(HttpStatus.CREATED);

        String locationUrl = createCompanyResponse.getHeaders().getLocation().toString();
        long id = Long.valueOf(locationUrl.substring(locationUrl.lastIndexOf("/") + 1)); // last segment should be id

        company.setId(id);
        List<Company> companies = restClient.findAllCompanies();
        assertThat(companies).as("Created company should be returned by find all").containsOnlyOnce(company);
    }

    @Test
    void findCompanyByIdThatDoesntExistShouldReturn404() {
        Throwable thrown = catchThrowable(() -> restClient.getCompany(-99999));
        assertThat(thrown)
                .as("Request to '/companies/{companyId}' with id that doesn't exist should return 404")
                .isInstanceOf(HttpClientErrorException.class)
                .hasMessageContaining(HttpStatus.NOT_FOUND.toString());
    }

    @Test
    void deleteCompanyThatDoesntExistShouldReturn404() {
        Throwable thrown = catchThrowable(() -> restClient.deleteCompany(-99999));
        assertThat(thrown)
                .as("DELETE request to '/companies/{companyId}' with id that doesn't exist should return 404")
                .isInstanceOf(HttpClientErrorException.class)
                .hasMessageContaining(HttpStatus.NOT_FOUND.toString());
    }

    @Test
    void addAndRemoveEmployeesFromCompanySuccessfully() {
        long companyId = createCompany();
        long employeeId = createEmployee();
        addEmployeeToCompanyOk(companyId, employeeId);
        removeEmployeeFromCompanyOk(companyId, employeeId);
    }

    @Test
    void addEmployeeToCompanyThatDoesntExistShouldReturn404() {
        Throwable thrown = catchThrowable(() -> restClient.addEmployeeToCompany(-99999, -9999));
        assertThat(thrown)
                .as("Adding employee to a company that doesn't exist should return 404")
                .isInstanceOf(HttpClientErrorException.class)
                .hasMessageContaining(HttpStatus.NOT_FOUND.toString());
    }

    @Test
    void addEmployeeThatDoesntExistToCompanyThatExistShouldReturn404() {
        long companyId = createCompany();
        Throwable thrown = catchThrowable(() -> restClient.addEmployeeToCompany(companyId, -9999));
        assertThat(thrown)
                .as("Adding employee that doesn't exist to a company should return 404")
                .isInstanceOf(HttpClientErrorException.class)
                .hasMessageContaining(HttpStatus.NOT_FOUND.toString());
    }

    @Test
    void addEmployeeToCompanyWhereEmployeeAlreadyBelongsShouldReturn400() {
        long companyId = createCompany();
        long employeeId = createEmployee();
        restClient.addEmployeeToCompany(companyId, employeeId);

        Throwable thrown = catchThrowable(() -> restClient.addEmployeeToCompany(companyId, employeeId));
        assertThat(thrown)
                .as("Adding employee to company where employee already belong should return 400")
                .isInstanceOf(HttpClientErrorException.class)
                .hasMessageContaining(HttpStatus.BAD_REQUEST.toString());
    }

    @Test
    void removeEmployeeFromCompanyThatDoesntExistShouldReturn404() {
        Throwable thrown = catchThrowable(() -> restClient.removeEmployeeFromCompany(-99999, -9999));
        assertThat(thrown)
                .as("Removing employee from a company that doesn't exist should return 404")
                .isInstanceOf(HttpClientErrorException.class)
                .hasMessageContaining(HttpStatus.NOT_FOUND.toString());
    }

    @Test
    void removeEmployeeThatDoesntExistFromCompanyThatExistShouldReturn400() {
        long companyId = createCompany();
        Throwable thrown = catchThrowable(() -> restClient.removeEmployeeFromCompany(companyId, -9999));
        assertThat(thrown)
                .as("Removing employee that doesn't exist from a company 400")
                .isInstanceOf(HttpClientErrorException.class)
                .hasMessageContaining(HttpStatus.BAD_REQUEST.toString());
    }

    @Test
    void removeEmployeeFromCompanyThatTheEmployeeDoesntBelongToShouldReturn422() {
        long companyId = createCompany();
        long employeeId = createEmployee();
        Throwable thrown = catchThrowable(() -> restClient.removeEmployeeFromCompany(companyId, employeeId));
        assertThat(thrown)
                .as("Removing employee from a company that the employee doesn't belong to should return 422")
                .isInstanceOf(HttpClientErrorException.class)
                .hasMessageContaining(HttpStatus.UNPROCESSABLE_ENTITY.toString());
    }

    private long createCompany() {
        Company companyToCreate = new Company();
        companyToCreate.setName("Company with employees!");
        ResponseEntity<Company> createCompanyResponse = restClient.createCompany(companyToCreate);
        assertThat(createCompanyResponse.getStatusCode()).as("Status code from create company should be 201").isEqualTo(HttpStatus.CREATED);
        assertThat(createCompanyResponse.getHeaders().getLocation()).as("Location header from create company should be returned").isNotNull();

        ResponseEntity<Company> readCompanyResponse = restClient.getCompany(createCompanyResponse.getHeaders().getLocation());
        assertThat(readCompanyResponse.getStatusCode()).as("Created company should be retrievable from location header").isEqualTo(HttpStatus.OK);
        Company createdCompany = readCompanyResponse.getBody();
        assertThat(createdCompany.getId()).as("Created company should be assigned an id").isNotNull();
        assertThat(createdCompany.getName()).as("Created company should get name from request").isEqualTo(companyToCreate.getName());
        assertThat(createdCompany.getEmployees()).as("Newly created company should not have any employees").hasSize(0);

        return createdCompany.getId();
    }

    private long createEmployee() {
        Employee employeeToCreate = new Employee("John", "Doe", LocalDate.of(1986, 7, 8));
        ResponseEntity<Employee> createEmployeeResponse = restClient.createEmployee(employeeToCreate);
        ResponseEntity<Employee> readEmployeeResponse = restClient.getEmployee(createEmployeeResponse.getHeaders().getLocation());
        return readEmployeeResponse.getBody().getId();
    }

    private void addEmployeeToCompanyOk(long companyId, long employeeId) {
        ResponseEntity<Company> addEmployeeToCompanyResponse = restClient.addEmployeeToCompany(companyId, employeeId);
        assertThat(addEmployeeToCompanyResponse.getStatusCode()).as("Status code from add employee to company should be 204").isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<Company> readCompanyResponse = restClient.getCompany(companyId);
        Company company = readCompanyResponse.getBody();
        assertThat(company.getEmployees()).as("Company should have 1 employee").hasSize(1);
        assertThat(company.getEmployees().iterator().next().getId()).as("Company should have employee that was added").isEqualTo(employeeId);
    }

    private void removeEmployeeFromCompanyOk(long companyId, long employeeId) {
        restClient.removeEmployeeFromCompany(companyId, employeeId);
        ResponseEntity<Company> readCompanyResponse = restClient.getCompany(companyId);
        assertThat(readCompanyResponse.getBody().getEmployees()).as("Company should have 0 employees after removal").hasSize(0);
    }
}
