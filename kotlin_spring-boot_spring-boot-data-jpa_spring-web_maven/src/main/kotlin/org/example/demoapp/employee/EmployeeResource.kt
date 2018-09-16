package org.example.demoapp.employee

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder

@RestController
@RequestMapping("employees")
class EmployeeResource(val employeeService: EmployeeService) {

    @GetMapping
    fun findAll(): List<Employee> {
        return employeeService.findAll()
    }

    @GetMapping("{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<Employee> {
        val employee = employeeService.findById(id)
        return if (employee.isPresent) {
            ResponseEntity.ok(employee.get())
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping
    fun post(@RequestBody employee: Employee, uri: UriComponentsBuilder): ResponseEntity<Any> {
        val savedEmployee = employeeService.save(employee)
        val path = uri.path("employees/${savedEmployee.id}").build().toUri()
        return ResponseEntity.created(path).build()
    }

    @PutMapping("{id}")
    fun put(@PathVariable id: Long, @RequestBody updatedEmployee: Employee): ResponseEntity<Any> {
        try {
            employeeService.updateEmployee(id, updatedEmployee)
        } catch (e: EmployeeNotFoundException) {
            return ResponseEntity.notFound().build()
        }
        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Any> {
        employeeService.delete(id)
        return ResponseEntity.noContent().build()

    }
}