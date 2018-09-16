package org.example.demoapp.employee;

import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.util.UriComponentsBuilder;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.BodyInserters.fromObject;
import static org.springframework.web.reactive.function.server.ServerResponse.created;
import static org.springframework.web.reactive.function.server.ServerResponse.noContent;
import static org.springframework.web.reactive.function.server.ServerResponse.notFound;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
public class EmployeeHandler {

    private final EmployeeService employeeService;

    public EmployeeHandler(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    public Mono<ServerResponse> findAll(ServerRequest serverRequest) {
        return ok().contentType(APPLICATION_JSON).body(fromObject(employeeService.findAll()));
    }

    public Mono<ServerResponse> findById(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");
        Optional<Employee> optionalEmployee = employeeService.findById(Long.valueOf(id));
        return optionalEmployee.map(employee -> ok().contentType(APPLICATION_JSON).body(fromObject(employee)))
                .orElseGet(() -> notFound().build());
    }

    public Mono<ServerResponse> create(ServerRequest serverRequest) {
        Mono<Employee> employeeMono = serverRequest.bodyToMono(Employee.class);
        Employee savedEmployee = employeeService.save(employeeMono);
        URI uri = UriComponentsBuilder.fromUri(serverRequest.uri()).pathSegment(String.valueOf(savedEmployee.getId())).build().toUri();
        return created(uri).build();
    }

    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        long employeeId = Long.valueOf(serverRequest.pathVariable("id"));
        Mono<Employee> employeeMono = serverRequest.bodyToMono(Employee.class);
        try {
            employeeService.updateEmployee(employeeId, employeeMono);
        } catch (EmployeeNotFoundException e) {
            return notFound().build();
        }

        return noContent().build();
    }

    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        long employeeId = Long.valueOf(serverRequest.pathVariable("id"));
        employeeService.deleteById(employeeId);
        return noContent().build();
    }
}
