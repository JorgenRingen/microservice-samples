package org.example.demoapp.resource

import org.example.demoapp.entity.Company
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit4.SpringRunner

import static org.assertj.core.api.AssertionsForClassTypes.assertThat

@RunWith(SpringRunner)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CompanyResourceTest {

    @Autowired
    private TestRestTemplate testRestTemplate

    @Test
    void createCompany() {
        def companyToCreate = new Company()
        companyToCreate.name = "test-company"
        def createCompanyResponse = testRestTemplate.postForEntity("/companies", companyToCreate, Company.class)
        assertThat(createCompanyResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED)

        def findCompanyResponse = testRestTemplate.getForEntity(createCompanyResponse.getHeaders().getLocation(), Company.class)
        assertThat(findCompanyResponse.getStatusCode()).isEqualTo(HttpStatus.OK)
        def company = findCompanyResponse.getBody()
        assertThat(company.getName()).isEqualTo(companyToCreate.name)
    }
}