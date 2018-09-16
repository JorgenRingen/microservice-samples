package org.example.demoapp.company

import org.example.demoapp.employee.EmployeeNotFoundException
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
    CompanyService companyService

    @GetMapping
    def findAll() {
        return ResponseEntity.ok(companyService.findAll())
    }

    @GetMapping("{id}")
    def findById(@PathVariable long id) {
        def optionalCompany = companyService.findById(id)
        if (optionalCompany.isPresent()) {
            return ResponseEntity.ok(optionalCompany.get())
        } else {
            return ResponseEntity.notFound().build()
        }
    }

    @PostMapping
    def post(@RequestBody Company company, UriComponentsBuilder uri) {
        def id = companyService.save(company).id
        def path = uri.path("companies/${id}").build().toUri()
        return ResponseEntity.created(path).build()
    }

    @DeleteMapping("{id}")
    def delete(@PathVariable long id) {
        companyService.delete(id)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("{companyId}/employees")
    def addEmployee(@PathVariable("companyId") long companyId, @RequestBody long employeeId) {
        try {
            companyService.addEmployee(companyId, employeeId)
        } catch (EmployeeNotFoundException | CompanyNotFoundException ignored) {
            return ResponseEntity.notFound().build()
        } catch (EmployeeAlreadeEmployedInCompanyException ignored) {
            return ResponseEntity.badRequest().body("Employee with id=$employeeId already employed in company with id=$companyId")
        }

        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("{companyId}/employees/{employeeId}")
    def removeEmployee(@PathVariable("companyId") long companyId, @PathVariable("employeeId") long employeeId) {
        companyService.removeEmployee(companyId, employeeId)
        return ResponseEntity.noContent().build()
    }
}
