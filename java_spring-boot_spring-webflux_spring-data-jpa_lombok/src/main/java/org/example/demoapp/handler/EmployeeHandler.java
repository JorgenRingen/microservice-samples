package org.example.demoapp.handler;

import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Optional;

import org.example.demoapp.entity.Employee;
import org.example.demoapp.repository.EmployeeRepository;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.util.UriComponentsBuilder;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.BodyInserters.fromObject;
import static org.springframework.web.reactive.function.server.ServerResponse.created;
import static org.springframework.web.reactive.function.server.ServerResponse.noContent;
import static org.springframework.web.reactive.function.server.ServerResponse.notFound;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

public class EmployeeHandler {

    private final EmployeeRepository employeeRepository;

    public EmployeeHandler(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public Mono<ServerResponse> findAll(ServerRequest serverRequest) {
        return ok().contentType(APPLICATION_JSON).body(fromObject(employeeRepository.findAll()));
    }

    public Mono<ServerResponse> findById(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");
        Optional<Employee> optionalEmployee = employeeRepository.findById(Long.valueOf(id));
        return optionalEmployee.map(employee -> ok().contentType(APPLICATION_JSON).body(fromObject(employee)))
                .orElseGet(() -> notFound().build());
    }

    public Mono<ServerResponse> create(ServerRequest serverRequest) {
        Employee employee = serverRequest.bodyToMono(Employee.class).toProcessor().block();
        employee = employeeRepository.save(employee);
        URI uri = UriComponentsBuilder.fromUri(serverRequest.uri()).pathSegment(String.valueOf(employee.getId())).build().toUri();
        return created(uri).build();
    }

    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        Long employeeId = Long.valueOf(serverRequest.pathVariable("id"));
        Optional<Employee> optionalEmployee = employeeRepository.findById(employeeId);
        if (optionalEmployee.isPresent()) {
            Employee existingEmployee = optionalEmployee.get();
            existingEmployee.update(serverRequest.bodyToMono(Employee.class).toProcessor().block());
            employeeRepository.save(existingEmployee);
            return noContent().build();
        } else {
            return notFound().build();
        }
    }

    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        Long employeeId = Long.valueOf(serverRequest.pathVariable("id"));
        Optional<Employee> optionalEmployee = employeeRepository.findById(employeeId);
        if (optionalEmployee.isPresent()) {
            employeeRepository.deleteById(employeeId);
            return noContent().build();
        } else {
            return notFound().build();
        }
    }
}
