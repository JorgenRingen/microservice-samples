package org.example.demoapp.resource;

import org.example.demoapp.entity.Company;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CompanyResourceTest {

    @LocalServerPort
    protected int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    void createCompany() {
        final String companyName = "test-company";
        ResponseEntity<Company> createCompanyResponse = testRestTemplate.postForEntity("/companies", Company.builder().name(companyName).build(), Company.class);
        assertThat(createCompanyResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<Company> findCompanyResponse = testRestTemplate.getForEntity(createCompanyResponse.getHeaders().getLocation(), Company.class);
        assertThat(findCompanyResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Company company = findCompanyResponse.getBody();
        assertThat(company.getName()).isEqualTo(companyName);
    }
}
