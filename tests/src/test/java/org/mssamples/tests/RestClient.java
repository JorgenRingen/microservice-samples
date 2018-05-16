package org.mssamples.tests;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import org.mssamples.tests.model.Company;
import org.mssamples.tests.model.Employee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import static org.assertj.core.api.Assertions.assertThat;

class RestClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestClient.class);
    private static final String APP_URL;

    static {
        var providedAppUrl = System.getProperty("appUrl");
        if (providedAppUrl != null) {
            LOGGER.debug("Running tests with provided application url: {}", providedAppUrl);
            APP_URL = providedAppUrl;
        } else {
            var defaultAppUrl = "http://localhost:8080/";
            LOGGER.debug("Application url not provided, running tests with default url: {}", defaultAppUrl);
            LOGGER.debug("You can provide a custom url by setting 'appUrl' command line argument (mvn test -DappUrl=http://yourUrl:1337)");
            APP_URL = defaultAppUrl;
        }
    }

    private final RestTemplate restTemplate;

    RestClient() {
        restTemplate = createRestTemplate();
    }

    private RestTemplate createRestTemplate() {
        HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpComponentsClientHttpRequestFactory.setConnectTimeout(5_000);
        httpComponentsClientHttpRequestFactory.setReadTimeout(5_000);
        return new RestTemplate(httpComponentsClientHttpRequestFactory);
    }

    // Employee

    List<Employee> findAllEmployees() {
        ResponseEntity<Employee[]> employeesResponseEntity = restTemplate.getForEntity(APP_URL + "/employees", Employee[].class);
        assertThat(employeesResponseEntity.getStatusCode()).as("/employees should always return 200").isEqualTo(HttpStatus.OK);
        return Arrays.asList(employeesResponseEntity.getBody());
    }

    ResponseEntity<Employee> getEmployee(long employeeId) {
        String uri = UriComponentsBuilder.fromHttpUrl(APP_URL).path("employees/" + employeeId).toUriString();
        return restTemplate.getForEntity(uri, Employee.class);
    }

    ResponseEntity<Employee> getEmployee(URI uri) {
        return restTemplate.getForEntity(uri, Employee.class);
    }

    ResponseEntity<Employee> createEmployee(Employee employee) {
        return restTemplate.postForEntity(APP_URL + "/employees", employee, Employee.class);
    }

    ResponseEntity<Employee> updateEmployee(Employee employeeToUpdate) {
        String uri = UriComponentsBuilder.fromHttpUrl(APP_URL).path("employees/" + employeeToUpdate.getId()).toUriString();
        return restTemplate.exchange(uri, HttpMethod.PUT, new HttpEntity<>(employeeToUpdate), Employee.class);
    }

    void deleteEmployee(long id) {
        String uri = UriComponentsBuilder.fromHttpUrl(APP_URL).path("employees/" + id).toUriString();
        restTemplate.delete(uri);
    }

    // Company

    List<Company> findAllCompanies() {
        ResponseEntity<Company[]> companiesResponseEntity = restTemplate.getForEntity(APP_URL + "/companies", Company[].class);
        assertThat(companiesResponseEntity.getStatusCode()).as("/companies should always return 200").isEqualTo(HttpStatus.OK);
        return Arrays.asList(companiesResponseEntity.getBody());
    }

    ResponseEntity<Company> createCompany(Company company) {
        return restTemplate.postForEntity(APP_URL + "/companies", company, Company.class);
    }

    ResponseEntity<Company> getCompany(long companyId) {
        String uri = UriComponentsBuilder.fromHttpUrl(APP_URL).path("/companies/" + companyId).toUriString();
        return restTemplate.getForEntity(uri, Company.class);
    }

    ResponseEntity<Company> getCompany(URI uri) {
        return restTemplate.getForEntity(uri, Company.class);
    }

    void deleteCompany(long companyId) {
        String uri = UriComponentsBuilder.fromHttpUrl(APP_URL).path("companies/" + companyId).toUriString();
        restTemplate.delete(uri);
    }

    ResponseEntity<Company> addEmployeeToCompany(long companyId, long employeeId) {
        return restTemplate.postForEntity(APP_URL + "/companies/" + companyId + "/employees", employeeId, Company.class);
    }

    void removeEmployeeFromCompany(long companyId, long employeeId) {
        restTemplate.delete(APP_URL + "/companies/" + companyId + "/employees/" + employeeId);
    }
}
