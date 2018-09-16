package org.example.demoapp.handler;

import java.time.LocalDate;

import org.example.demoapp.employee.Employee;
import org.example.demoapp.employee.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EmployeeHandlerTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @LocalServerPort
    private int port;

    private WebTestClient webTestClient;

    @BeforeEach
    void beforeEach() {
        webTestClient = WebTestClient.bindToServer().baseUrl("http://localhost:" + port)
                .build();
    }

    @Test
    void findEmployeeById() {
        long savedEmployeeId = employeeRepository.save(Employee.builder()
                .firstname("Test")
                .lastname("Testersen")
                .dateOfBirth(LocalDate.of(1999, 1, 1))
                .build())
                .getId();

        webTestClient.get()
                .uri("employees/" + savedEmployeeId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Employee.class)
                .consumeWith(result -> {
                    Employee employee = result.getResponseBody();
                    assertThat(employee).isNotNull();
                    assertThat(employee.getFirstname()).isEqualTo("Test");
                    assertThat(employee.getLastname()).isEqualTo("Testersen");
                });
    }
}