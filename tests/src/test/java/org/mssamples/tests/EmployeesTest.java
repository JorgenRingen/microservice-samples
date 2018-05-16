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
        List<Employee> employees = restClient.getAllEmployees();
        assertThat(employees).isNotNull();
    }

    @Test
    void findAllShouldReturnEmployeeThatExists() {
        var firstname = "John";
        var lastname = "Doe";
        var dateOfBirth = LocalDate.of(1986, 7, 8);
        Employee employee = new Employee(firstname, lastname, dateOfBirth);

        ResponseEntity<Employee> createEmployeeResponse = restClient.createEmployee(employee);
        assertThat(createEmployeeResponse.getStatusCode()).as("Status code from create employee should be 201").isEqualTo(HttpStatus.CREATED);

        String locationUrl = createEmployeeResponse.getHeaders().getLocation().toString();
        long id = Long.valueOf(locationUrl.substring(locationUrl.lastIndexOf("/") + 1)); // last segment should be id

        employee.setId(id);
        List<Employee> employees = restClient.getAllEmployees();
        assertThat(employees).as("Created employee should be returned by find all").containsOnlyOnce(employee);
    }

    @Test
    void findEmployeeByIdThatDoesntExistShouldReturn404() {
        Throwable thrown = catchThrowable(() -> restClient.getEmployee(-99999));
        assertThat(thrown)
                .as("Request to '/employees/{employeeId}' with id that doesn't exist should return 404")
                .isInstanceOf(HttpClientErrorException.class)
                .hasMessageContaining(HttpStatus.NOT_FOUND.toString());
    }

    @Test
    void updateEmployeeThatDoesntExistShouldReturn404() {
        Throwable thrown = catchThrowable(() -> restClient.getEmployee(-99999));
        assertThat(thrown)
                .as("Request to '/employees/{employeeId}' with id that doesn't exist should return 404")
                .isInstanceOf(HttpClientErrorException.class)
                .hasMessageContaining(HttpStatus.NOT_FOUND.toString());
    }

    @Test
    void crudEmployeeShouldWorkAccordingToHttpSpec() {
        // create
        var firstname = "John";
        var lastname = "Doe";
        var dateOfBirth = LocalDate.of(1986, 7, 8);

        Employee employeeToCreate = new Employee(firstname, lastname, dateOfBirth);
        ResponseEntity<Employee> createEmployeeResponse = restClient.createEmployee(employeeToCreate);
        assertThat(createEmployeeResponse.getStatusCode()).as("Status code from create employee should be 201").isEqualTo(HttpStatus.CREATED);
        assertThat(createEmployeeResponse.getHeaders().getLocation()).as("Location header from create employee should be returned").isNotNull();

        // read
        ResponseEntity<Employee> readCreatedEmployeeResponse = restClient.getEmployee(createEmployeeResponse.getHeaders().getLocation());
        assertThat(readCreatedEmployeeResponse.getStatusCode()).as("Created employee should be retrievable from location header").isEqualTo(HttpStatus.OK);
        Employee createdEmployee = readCreatedEmployeeResponse.getBody();
        assertThat(createdEmployee.getId()).as("Created employee should be assigned an id").isNotNull();
        assertThat(createdEmployee.getFirstName()).as("Created employee should get firstname from request").isEqualTo(firstname);
        assertThat(createdEmployee.getLastName()).as("Created employee should get lastname from request").isEqualTo(lastname);
        assertThat(createdEmployee.getDateOfBirth()).as("Created employee should get dateOfBirth from request").isEqualTo(dateOfBirth);

        // update
        var updatedFirstname = "Jane";
        var updatedLastName = "Smith";
        var updatedDateOfBirth = LocalDate.of(1999, 1, 1);
        Employee employeeToUpdate = new Employee(createdEmployee.getId(), updatedFirstname, updatedLastName, updatedDateOfBirth);

        ResponseEntity<Employee> updateEmployeeResponse = restClient.updateEmployee(employeeToUpdate);
        assertThat(updateEmployeeResponse.getStatusCode()).as("Status code from update employee should be 200").isEqualTo(HttpStatus.OK);

        Employee updatedEmployee = updateEmployeeResponse.getBody();
        assertThat(updatedEmployee.getId()).as("Updated employee should have an id").isEqualTo(employeeToUpdate.getId());
        assertThat(updatedEmployee.getFirstName()).as("Updated employee should get firstname from request").isEqualTo(updatedFirstname);
        assertThat(updatedEmployee.getLastName()).as("Updated employee should get lastname from request").isEqualTo(updatedLastName);
        assertThat(updatedEmployee.getDateOfBirth()).as("Updated employee should get dateOfBirth from request").isEqualTo(updatedDateOfBirth);

        // delete
        Throwable thrownByDelete = catchThrowable(() -> restClient.deleteEmployee(createdEmployee.getId()));
        assertThat(thrownByDelete)
                .as("Delete existing employee should not throw exception")
                .doesNotThrowAnyException();

        // read deleted
        Throwable thrownByRead = catchThrowable(() -> restClient.getEmployee(createdEmployee.getId()));
        assertThat(thrownByRead)
                .as("Find deleted employee should return 404")
                .isInstanceOf(HttpClientErrorException.class)
                .hasMessageContaining(HttpStatus.NOT_FOUND.toString());
    }
}
