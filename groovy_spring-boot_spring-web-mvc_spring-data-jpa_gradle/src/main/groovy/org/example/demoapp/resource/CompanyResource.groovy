package org.example.demoapp.resource

import org.example.demoapp.entity.Company
import org.example.demoapp.repository.CompanyRepository
import org.example.demoapp.repository.EmployeeRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.util.UriComponentsBuilder

@RestController
@RequestMapping("companies")
class CompanyResource {

    @Autowired
    CompanyRepository companyRepository

    @Autowired
    EmployeeRepository employeeRepository

    @GetMapping
    def findAll() {
        return ResponseEntity.ok(companyRepository.findAll())
    }

    @GetMapping("{id}")
    def findById(@PathVariable long id) {
        def optionalCompany = companyRepository.findById(id)
        return optionalCompany.map({
            company -> return ResponseEntity.ok(company)
        }).orElse({
            return ResponseEntity.notFound().build()
        })
    }

    @PostMapping
    def post(@RequestBody Company company, UriComponentsBuilder uri) {
        def id = companyRepository.save(company).id
        def path = uri.path("companies/${id}").build().toUri()
        return ResponseEntity.created(path).build()
    }

    @DeleteMapping("{id}")
    def delete(@PathVariable long id) {
        def optionalCompany = companyRepository.findById(id)
        if (optionalCompany.isPresent()) {
            companyRepository.deleteById(id)
            return ResponseEntity.noContent().build()
        } else {
            return ResponseEntity.notFound().build()
        }
    }

    @PostMapping("{companyId}/employees")
    def addEmployee(@PathVariable("companyId") long companyId, @RequestBody long employeeId) {
        def optionalCompany = companyRepository.findById(companyId)
        if (!optionalCompany.isPresent()) {
            return ResponseEntity.notFound().build()
        }

        def optionalEmployee = employeeRepository.findById(employeeId)
        if (!optionalEmployee.isPresent()) {
            return ResponseEntity.notFound().build()
        }

        def company = optionalCompany.get()
        def employeeAlreadyEmployed = company.isEmployeeEmployed(employeeId)

        if (employeeAlreadyEmployed) {
            return ResponseEntity.badRequest().body("Employee with id=${employeeId} already in company with id=${companyId}")
        } else {
            company.addEmployee(optionalEmployee.get())
            companyRepository.save(company)
            return ResponseEntity.noContent().build()
        }
    }

    @DeleteMapping("{companyId}/employees/{employeeId}")
    def removeEmployee(@PathVariable("companyId") long companyId, @PathVariable("employeeId") long employeeId) {
        def optionalCompany = companyRepository.findById(companyId)
        if (!optionalCompany.isPresent()) {
            return ResponseEntity.notFound().build()
        }

        def optionalEmployee = employeeRepository.findById(employeeId)
        if (!optionalEmployee.isPresent()) {
            return ResponseEntity.notFound().build()
        }

        def company = optionalCompany.get()
        company.removeEmployee(optionalEmployee.get())
        companyRepository.save(company)

        return ResponseEntity.noContent().build()
    }
}
