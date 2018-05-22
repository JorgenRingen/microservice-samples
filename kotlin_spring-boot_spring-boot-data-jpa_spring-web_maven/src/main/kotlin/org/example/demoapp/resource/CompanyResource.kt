package org.example.demoapp.resource

import org.example.demoapp.entity.Company
import org.example.demoapp.repository.CompanyRepository
import org.example.demoapp.repository.EmployeeRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder

@RestController
@RequestMapping("companies")
class CompanyResource(val companyRepository: CompanyRepository,
                      val employeeRepository: EmployeeRepository) {

    @GetMapping
    fun findAll(): List<Company> {
        return companyRepository.findAll()
    }

    @GetMapping("{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<Company> {
        val company = companyRepository.findById(id)
        return if (company.isPresent) {
            ResponseEntity.ok(company.get())
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping
    fun post(@RequestBody company: Company, uri: UriComponentsBuilder): ResponseEntity<Any> {
        val savedCompany = companyRepository.save(company)
        val path = uri.path("companies/${savedCompany.id}").build().toUri()
        return ResponseEntity.created(path).build()
    }

    @DeleteMapping("{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Any> {
        val optionalCompany = companyRepository.findById(id)
        return if (optionalCompany.isPresent) {
            companyRepository.delete(optionalCompany.get())
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping("{companyId}/employees")
    fun addEmployee(@PathVariable("companyId") companyId: Long, @RequestBody employeeId: Long): ResponseEntity<Any> {
        val optionalCompany = companyRepository.findById(companyId)
        if (!optionalCompany.isPresent) {
            return ResponseEntity.notFound().build()
        }

        val optionalEmployee = employeeRepository.findById(employeeId)
        if (!optionalEmployee.isPresent) {
            return ResponseEntity.notFound().build()
        }

        val company = optionalCompany.get()
        val employeeAlreadyInCompany = company.employees.any { employee -> employee.id == employeeId }

        return if (employeeAlreadyInCompany) {
            ResponseEntity.badRequest().body("Employee with id=$employeeId already in company with id=$companyId")
        } else {
            company.employees.add(optionalEmployee.get())
            companyRepository.save(company)
            ResponseEntity.noContent().build()
        }
    }

    @DeleteMapping("{companyId}/employees/{employeeId}")
    fun removeEmployee(@PathVariable("companyId") companyId: Long, @PathVariable("employeeId") employeeId: Long): ResponseEntity<Any> {
        val optionalCompany = companyRepository.findById(companyId)
        if (!optionalCompany.isPresent) {
            return ResponseEntity.notFound().build()
        }

        val optionalEmployee = employeeRepository.findById(employeeId)
        if (!optionalEmployee.isPresent) {
            return ResponseEntity.notFound().build()
        }

        val company = optionalCompany.get()
        val employeeRemoved = company.employees.removeIf { employee -> employee.id == employeeId }
        if (employeeRemoved) {
            companyRepository.save(company)
        }

        return ResponseEntity.noContent().build()
    }
}