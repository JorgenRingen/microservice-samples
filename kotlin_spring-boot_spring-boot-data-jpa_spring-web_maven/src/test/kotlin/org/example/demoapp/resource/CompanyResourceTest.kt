package org.example.demoapp.resource

import org.assertj.core.api.Assertions.assertThat
import org.example.demoapp.entity.Company
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit.jupiter.SpringExtension


@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CompanyResourceTest(@Autowired private val testRestTemplate: TestRestTemplate) {

    @Test
    fun createCompany() {
        val createCompanyResponse = testRestTemplate.postForEntity("/companies", Company(name = "test-company"), Company::class.java)
        assertThat(createCompanyResponse.statusCode).isEqualTo(HttpStatus.CREATED)

        val findCompanyResponse = testRestTemplate.getForEntity(createCompanyResponse.headers.location, Company::class.java)
        assertThat(findCompanyResponse.statusCode).isEqualTo(HttpStatus.OK)
        val company = findCompanyResponse.body
        assertThat(company!!.name).isEqualTo("test-company")
    }
}