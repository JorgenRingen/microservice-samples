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
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import static org.assertj.core.api.Assertions.assertThat;

class RestClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestClient.class);
    private static final String APP_URL;

    static {
        String providedAppUrl = System.getProperty("appUrl");
        if (providedAppUrl != null) {
            LOGGER.debug("Running tests with provided application url: {}", providedAppUrl);
            APP_URL = providedAppUrl;
        } else {
            String defaultAppUrl = "http://localhost:8080/";
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
        httpComponentsClientHttpRequestFactory.setConnectTimeout(10_000);
        httpComponentsClientHttpRequestFactory.setReadTimeout(10_000);

        RestTemplate restTemplate = new RestTemplate(httpComponentsClientHttpRequestFactory);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        messageConverter.setPrettyPrint(false);
        messageConverter.setObjectMapper(objectMapper);
        restTemplate.getMessageConverters().removeIf(m -> m.getClass().getName().equals(MappingJackson2HttpMessageConverter.class.getName()));
        restTemplate.getMessageConverters().add(messageConverter);

        return restTemplate;
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

    ResponseEntity deleteEmployee(long employeeId) {
        String uri = UriComponentsBuilder.fromHttpUrl(APP_URL).path("employees/" + employeeId).toUriString();
        return restTemplate.exchange(uri, HttpMethod.DELETE, null, Object.class);
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

    ResponseEntity deleteCompany(long companyId) {
        String uri = UriComponentsBuilder.fromHttpUrl(APP_URL).path("companies/" + companyId).toUriString();
        return restTemplate.exchange(uri, HttpMethod.DELETE, null, Object.class);
    }

    ResponseEntity<Company> addEmployeeToCompany(long companyId, long employeeId) {
        return restTemplate.postForEntity(APP_URL + "/companies/" + companyId + "/employees", employeeId, Company.class);
    }

    ResponseEntity removeEmployeeFromCompany(long companyId, long employeeId) {
        return restTemplate.exchange(APP_URL + "/companies/" + companyId + "/employees/" + employeeId, HttpMethod.DELETE, null, Object.class);
    }
}
