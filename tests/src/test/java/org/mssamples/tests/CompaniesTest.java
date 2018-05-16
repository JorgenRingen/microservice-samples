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
    void addAndRemoveEmployeesFromCompany() {
        long companyId = createCompany();
        long employeeId = createEmployeeAndAddToCompany(companyId);
        removeEmployeeFromCompany(companyId, employeeId);
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

    private long createEmployeeAndAddToCompany(long companyId) {
        Employee employee = new Employee("John", "Doe", LocalDate.of(1986, 7, 8));
        ResponseEntity<Employee> createEmployeeResponse = restClient.createEmployee(employee);
        ResponseEntity<Employee> readEmployeeResponse = restClient.getEmployee(createEmployeeResponse.getHeaders().getLocation());
        long employeeId = readEmployeeResponse.getBody().getId();

        ResponseEntity<Company> addEmployeeToCompanyResponse = restClient.addEmployeeToCompany(companyId, employeeId);
        assertThat(addEmployeeToCompanyResponse.getStatusCode()).as("Status code from add employee to company should be 200").isEqualTo(HttpStatus.OK);

        ResponseEntity<Company> readCompanyResponse = restClient.getCompany(companyId);
        Company company = readCompanyResponse.getBody();
        assertThat(company.getEmployees()).as("Company should have 1 employee").hasSize(1);
        assertThat(company.getEmployees().iterator().next().getId()).as("Company should have employee that was added").isEqualTo(employeeId);

        return employeeId;
    }

    private void removeEmployeeFromCompany(long companyId, long employeeId) {
        restClient.removeEmployeeFromCompany(companyId, employeeId);
        ResponseEntity<Company> readCompanyResponse = restClient.getCompany(companyId);
        assertThat(readCompanyResponse.getBody().getEmployees()).as("Company should have 0 employees after removal").hasSize(0);
    }
}
