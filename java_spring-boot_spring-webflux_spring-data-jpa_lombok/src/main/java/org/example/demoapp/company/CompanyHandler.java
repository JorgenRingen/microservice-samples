package org.example.demoapp.company;

import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Optional;

import org.example.demoapp.employee.EmployeeNotFoundException;
import org.springframework.stereotype.Component;
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

@Component
public class CompanyHandler {

    private final CompanyService companyService;

    public CompanyHandler(CompanyService companyService) {
        this.companyService = companyService;
    }

    public Mono<ServerResponse> findAll(ServerRequest serverRequest) {
        return ok().contentType(APPLICATION_JSON).body(fromObject(companyService.findAll()));
    }

    public Mono<ServerResponse> findById(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");
        Optional<Company> optionalCompany = companyService.findById(Long.valueOf(id));
        return optionalCompany.map(company -> ok().contentType(APPLICATION_JSON).body(fromObject(company)))
                .orElseGet(() -> notFound().build());
    }

    public Mono<ServerResponse> create(ServerRequest serverRequest) {
        Company company = serverRequest.bodyToMono(Company.class).toProcessor().block();
        company = companyService.save(company);
        URI uri = UriComponentsBuilder.fromUri(serverRequest.uri()).pathSegment(String.valueOf(company.getId())).build().toUri();
        return created(uri).build();
    }

    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        long companyId = Long.valueOf(serverRequest.pathVariable("id"));
        companyService.delete(companyId);
        return noContent().build();
    }

    public Mono<ServerResponse> addEmployee(ServerRequest serverRequest) {
        long companyId = Long.valueOf(serverRequest.pathVariable("companyId"));
        Mono<Long> employeeIdMono = serverRequest.bodyToMono(Long.class);
        try {
            companyService.addEmployee(companyId, employeeIdMono);
        } catch (EmployeeNotFoundException | CompanyNotFoundException e) {
            return notFound().build();
        } catch (EmployeeAlreadyEmployedInCompanyException e) {
            return badRequest().body(fromObject(e.getMessage()));
        }

        return noContent().build();
    }

    public Mono<ServerResponse> removeEmployee(ServerRequest serverRequest) {
        long companyId = Long.valueOf(serverRequest.pathVariable("companyId"));
        long employeeId = Long.valueOf(serverRequest.pathVariable("employeeId"));
        companyService.removeEmployee(companyId, employeeId);
        return noContent().build();
    }

}
