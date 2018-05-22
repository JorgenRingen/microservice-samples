package org.example.demo.handler;

import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

import org.example.demo.entity.Employee;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.BodyInserters.fromObject;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

public class EmployeeHandler {

    public Mono<ServerResponse> listEmployees(ServerRequest serverRequest) {
        List<Employee> employees = Collections.singletonList(Employee.builder().firstname("Jorgen").lastname("Ringen").build());
        return ok().contentType(APPLICATION_JSON).body(fromObject(employees));
    }
}
