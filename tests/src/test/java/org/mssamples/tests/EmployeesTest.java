package org.mssamples.tests;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mssamples.tests.model.Employee;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class EmployeesTest {

    private RestClient restClient = new RestClient();

    @Test
    void findAllEmployeesShouldReturn200() {
        List<Employee> employees = restClient.findAllEmployees();
        assertThat(employees).isNotNull();
    }

    @Test
    void findAllShouldReturnEmployeeThatExists() {
        String firstname = "John";
        String lastname = "Doe";
        LocalDate dateOfBirth = LocalDate.of(1986, 7, 8);
        Employee employee = new Employee(firstname, lastname, dateOfBirth);

        ResponseEntity<Employee> createEmployeeResponse = restClient.createEmployee(employee);
        assertThat(createEmployeeResponse.getStatusCode()).as("Status code from create employee should be 201").isEqualTo(HttpStatus.CREATED);

        String locationUrl = createEmployeeResponse.getHeaders().getLocation().toString();
        long id = Long.valueOf(locationUrl.substring(locationUrl.lastIndexOf("/") + 1)); // last segment should be id

        employee.setId(id);
        List<Employee> employees = restClient.findAllEmployees();
        assertThat(employees).as("Created employee should be returned by find all").containsOnlyOnce(employee);
    }

    @Test
    void findEmployeeByIdThatDoesntExistShouldReturn404() {
        Throwable thrown = catchThrowable(() -> restClient.getEmployee(-99999));
        assertThat(thrown)
                .as("GET request to '/employees/{employeeId}' with id that doesn't exist should return 404")
                .isInstanceOf(HttpClientErrorException.class)
                .hasMessageContaining(HttpStatus.NOT_FOUND.toString());
    }

    @Test
    void updateEmployeeThatDoesntExistShouldReturn404() {
        Throwable thrown = catchThrowable(() -> restClient.updateEmployee(new Employee(-99999L, null, null, null)));
        assertThat(thrown)
                .as("PUT request to '/employees/{employeeId}' with id that doesn't exist should return 404")
                .isInstanceOf(HttpClientErrorException.class)
                .hasMessageContaining(HttpStatus.NOT_FOUND.toString());
    }

    @Test
    void deleteEmployeeThatDoesntExistShouldReturn404() {
        ResponseEntity responseEntity = restClient.deleteEmployee(-99999L);
        assertThat(responseEntity.getStatusCode()).as("DELETE request to '/employees/{employeeId}' with id that doesn't exist should return 204").isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void crudEmployeeShouldWorkAccordingToApiSpec() {
        long employeeId = createEmployee();
        updateEmployee(employeeId);
        deleteEmployee(employeeId);
    }

    private long createEmployee() {
        Employee employeeToCreate = new Employee("John", "Doe", LocalDate.of(1986, 7, 8));
        ResponseEntity<Employee> createEmployeeResponse = restClient.createEmployee(employeeToCreate);
        assertThat(createEmployeeResponse.getStatusCode()).as("Status code from create employee should be 201").isEqualTo(HttpStatus.CREATED);
        assertThat(createEmployeeResponse.getHeaders().getLocation()).as("Location header from create employee should be returned").isNotNull();

        ResponseEntity<Employee> readEmployeeResponse = restClient.getEmployee(createEmployeeResponse.getHeaders().getLocation());
        assertThat(readEmployeeResponse.getStatusCode()).as("Created employee should be retrievable from location header").isEqualTo(HttpStatus.OK);
        Employee createdEmployee = readEmployeeResponse.getBody();
        assertThat(createdEmployee.getId()).as("Created employee should be assigned an id").isNotNull();
        assertThat(createdEmployee.getFirstname()).as("Created employee should get firstname from request").isEqualTo(employeeToCreate.getFirstname());
        assertThat(createdEmployee.getLastname()).as("Created employee should get lastname from request").isEqualTo(employeeToCreate.getLastname());
        assertThat(createdEmployee.getDateOfBirth()).as("Created employee should get dateOfBirth from request").isEqualTo(employeeToCreate.getDateOfBirth());

        return createdEmployee.getId();
    }

    private void updateEmployee(long employeeId) {
        Employee employeeToUpdate = new Employee(employeeId, "Jane", "Smith", LocalDate.of(1999, 1, 1));
        restClient.updateEmployee(employeeToUpdate);

        ResponseEntity<Employee> readEmployeeResponse = restClient.getEmployee(employeeId);
        Employee updatedEmployee = readEmployeeResponse.getBody();
        assertThat(updatedEmployee.getId()).as("Updated employee should have an id").isEqualTo(employeeToUpdate.getId());
        assertThat(updatedEmployee.getFirstname()).as("Updated employee should get firstname from request").isEqualTo(employeeToUpdate.getFirstname());
        assertThat(updatedEmployee.getLastname()).as("Updated employee should get lastname from request").isEqualTo(employeeToUpdate.getLastname());
        assertThat(updatedEmployee.getDateOfBirth()).as("Updated employee should get dateOfBirth from request").isEqualTo(employeeToUpdate.getDateOfBirth());
    }

    private void deleteEmployee(long employeeId) {
        Throwable thrownByDelete = catchThrowable(() -> restClient.deleteEmployee(employeeId));
        assertThat(thrownByDelete)
                .as("Delete existing employee should not throw exception")
                .doesNotThrowAnyException();

        Throwable thrownByRead = catchThrowable(() -> restClient.getEmployee(employeeId));
        assertThat(thrownByRead)
                .as("Find deleted employee should return 404")
                .isInstanceOf(HttpClientErrorException.class)
                .hasMessageContaining(HttpStatus.NOT_FOUND.toString());
    }
}
