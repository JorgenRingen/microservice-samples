package org.example.demoapp.company

import org.example.demoapp.employee.EmployeeNotFoundException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder

@RestController
@RequestMapping("companies")
class CompanyResource(val companyService: CompanyService) {

    @GetMapping
    fun findAll(): List<Company> {
        return companyService.findAll()
    }

    @GetMapping("{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<Company> {
        val company = companyService.findById(id)
        return if (company.isPresent) {
            ResponseEntity.ok(company.get())
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping
    fun post(@RequestBody company: Company, uri: UriComponentsBuilder): ResponseEntity<Any> {
        val savedCompany = companyService.save(company)
        val path = uri.path("companies/${savedCompany.id}").build().toUri()
        return ResponseEntity.created(path).build()
    }

    @DeleteMapping("{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Any> {
        companyService.delete(id)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("{companyId}/employees")
    fun addEmployee(@PathVariable("companyId") companyId: Long, @RequestBody employeeId: Long): ResponseEntity<Any> {
        try {
            companyService.addEmployee(companyId, employeeId)
        } catch (e: EmployeeNotFoundException) {
            return ResponseEntity.notFound().build()
        } catch (e: CompanyNotFoundException) {
            return ResponseEntity.notFound().build()
        } catch (e: EmployeeAlreadyEmployedInCompanyException) {
            return ResponseEntity.badRequest().body("Employee with id=$employeeId already in company with id=$companyId")
        }

        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("{companyId}/employees/{employeeId}")
    fun removeEmployee(@PathVariable("companyId") companyId: Long, @PathVariable("employeeId") employeeId: Long): ResponseEntity<Any> {
        companyService.removeEmployee(companyId, employeeId)
        return ResponseEntity.noContent().build()
    }
}