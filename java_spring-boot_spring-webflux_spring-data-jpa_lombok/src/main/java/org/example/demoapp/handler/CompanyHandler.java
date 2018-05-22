package org.example.demoapp.handler;

import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.Duration;
import java.util.Optional;

import org.example.demoapp.entity.Company;
import org.example.demoapp.entity.Employee;
import org.example.demoapp.repository.CompanyRepository;
import org.example.demoapp.repository.EmployeeRepository;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.util.UriComponentsBuilder;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.BodyInserters.fromObject;
import static org.springframework.web.reactive.function.server.ServerResponse.badRequest;
import static org.springframework.web.reactive.function.server.ServerResponse.created;
import static org.springframework.web.reactive.function.server.ServerResponse.noContent;
import static org.springframework.web.reactive.function.server.ServerResponse.notFound;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

public class CompanyHandler {

    private final CompanyRepository companyRepository;
    private final EmployeeRepository employeeRepository;

    public CompanyHandler(CompanyRepository companyRepository, EmployeeRepository employeeRepository) {
        this.companyRepository = companyRepository;
        this.employeeRepository = employeeRepository;
    }

    public Mono<ServerResponse> findAll(ServerRequest serverRequest) {
        return ok().contentType(APPLICATION_JSON).body(fromObject(companyRepository.findAll()));
    }

    public Mono<ServerResponse> findById(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");
        Optional<Company> optionalCompany = companyRepository.findById(Long.valueOf(id));
        return optionalCompany.map(company -> ok().contentType(APPLICATION_JSON).body(fromObject(company)))
                .orElseGet(() -> notFound().build());
    }

    public Mono<ServerResponse> create(ServerRequest serverRequest) {
        Company company = serverRequest.bodyToMono(Company.class).toProcessor().block();
        company = companyRepository.save(company);
        URI uri = UriComponentsBuilder.fromUri(serverRequest.uri()).pathSegment(String.valueOf(company.getId())).build().toUri();
        return created(uri).build();
    }

    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        Long companyId = Long.valueOf(serverRequest.pathVariable("id"));
        Optional<Company> optionalCompany = companyRepository.findById(companyId);
        if (optionalCompany.isPresent()) {
            companyRepository.deleteById(companyId);
            return noContent().build();
        } else {
            return notFound().build();
        }
    }

    public Mono<ServerResponse> addEmployee(ServerRequest serverRequest) {
        long companyId = Long.valueOf(serverRequest.pathVariable("companyId"));
        Optional<Company> optionalCompany = companyRepository.findById(companyId);
        if (!optionalCompany.isPresent()) {
            return notFound().build();
        }

        long employeeId = serverRequest.bodyToMono(Long.class).toProcessor().block();
        Optional<Employee> optionalEmployee = employeeRepository.findById(employeeId);
        if (!optionalEmployee.isPresent()) {
            return notFound().build();
        }

        Company company = optionalCompany.get();
        boolean employeeAlreadyEmployed = company.isEmployeeEmployed(employeeId);

        if (employeeAlreadyEmployed) {
            return badRequest().body(fromObject("Employee with id=" + employeeId + " already in company with id=" + companyId));
        } else {
            company.addEmployee(optionalEmployee.get());
            companyRepository.save(company);
            return noContent().build();
        }
    }

    public Mono<ServerResponse> removeEmployee(ServerRequest serverRequest) {
        long companyId = Long.valueOf(serverRequest.pathVariable("companyId"));
        Optional<Company> optionalCompany = companyRepository.findById(companyId);
        if (!optionalCompany.isPresent()) {
            return notFound().build();
        }

        long employeeId = Long.valueOf(serverRequest.pathVariable("employeeId"));
        Optional<Employee> optionalEmployee = employeeRepository.findById(employeeId);
        if (!optionalEmployee.isPresent()) {
            return notFound().build();
        }

        Company company = optionalCompany.get();
        company.removeEmployee(optionalEmployee.get());
        companyRepository.save(company);

        return noContent().build();
    }

}
