package org.example.demoapp.resource

import org.example.demoapp.entity.Employee
import org.example.demoapp.repository.EmployeeRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder

@RestController
@RequestMapping("employees")
class EmployeeResource(val employeeRepository: EmployeeRepository) {

    @GetMapping
    fun findAll(): List<Employee> {
        return employeeRepository.findAll()
    }

    @GetMapping("{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<Employee> {
        val employee = employeeRepository.findById(id)
        return if (employee.isPresent) {
            ResponseEntity.ok(employee.get())
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping
    fun post(@RequestBody employee: Employee, uri: UriComponentsBuilder): ResponseEntity<Any> {
        val savedEmployee = employeeRepository.save(employee)
        val path = uri.path("employees/${savedEmployee.id}").build().toUri()
        return ResponseEntity.created(path).build()
    }

    @PutMapping("{id}")
    fun put(@PathVariable id: Long, @RequestBody updatedEmployee: Employee): ResponseEntity<Any> {
        val optionalEmployee = employeeRepository.findById(id)
        return if (optionalEmployee.isPresent) {
            updatedEmployee.id = optionalEmployee.get().id
            employeeRepository.save(updatedEmployee)
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Any> {
        val optionalEmployee = employeeRepository.findById(id)
        return if (optionalEmployee.isPresent) {
            employeeRepository.delete(optionalEmployee.get())
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }
}