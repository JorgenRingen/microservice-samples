package org.example.demoapp;

import org.example.demoapp.handler.CompanyHandler;
import org.example.demoapp.handler.EmployeeHandler;
import org.example.demoapp.repository.CompanyRepository;
import org.example.demoapp.repository.EmployeeRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.function.server.RouterFunction;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.PUT;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@SpringBootApplication
@EnableWebFlux
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Bean
    public RouterFunction<?> employeeRouter(EmployeeHandler employeeHandler) {
        return route(GET("/employees").and(accept(APPLICATION_JSON)), employeeHandler::findAll)
                .andRoute(POST("/employees").and(accept(APPLICATION_JSON)), employeeHandler::create)
                .andRoute(GET("/employees/{id}").and(accept(APPLICATION_JSON)), employeeHandler::findById)
                .andRoute(PUT("/employees/{id}").and(accept(APPLICATION_JSON)), employeeHandler::update)
                .andRoute(DELETE("/employees/{id}").and(accept(APPLICATION_JSON)), employeeHandler::delete);
    }

    @Bean
    public RouterFunction<?> companyRouter(CompanyHandler companyHandler) {
        return route(GET("/companies").and(accept(APPLICATION_JSON)), companyHandler::findAll)
                .andRoute(POST("/companies").and(accept(APPLICATION_JSON)), companyHandler::create)
                .andRoute(GET("/companies/{id}").and(accept(APPLICATION_JSON)), companyHandler::findById)
                .andRoute(DELETE("/companies/{id}").and(accept(APPLICATION_JSON)), companyHandler::delete)
                .andRoute(POST("/companies/{companyId}/employees").and(accept(APPLICATION_JSON)), companyHandler::addEmployee)
                .andRoute(DELETE("/companies/{companyId}/employees/{employeeId}").and(accept(APPLICATION_JSON)), companyHandler::removeEmployee);
    }

    @Bean
    public EmployeeHandler employeeHandler(EmployeeRepository employeeRepository) {
        return new EmployeeHandler(employeeRepository);
    }

    @Bean
    public CompanyHandler companyHandler(CompanyRepository companyRepository, EmployeeRepository employeeRepository) {
        return new CompanyHandler(companyRepository, employeeRepository);
    }
}
