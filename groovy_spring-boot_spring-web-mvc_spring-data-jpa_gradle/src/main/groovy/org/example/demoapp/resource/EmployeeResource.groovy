package org.example.demoapp.resource

import org.example.demoapp.entity.Employee
import org.example.demoapp.repository.EmployeeRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.util.UriComponentsBuilder

@RestController
@RequestMapping("employees")
class EmployeeResource {

    @Autowired
    EmployeeRepository employeeRepository

    @GetMapping
    def findAll() {
        return ResponseEntity.ok(employeeRepository.findAll())
    }

    @GetMapping("{id}")
    def findById(@PathVariable long id) {
        def optionalEmployee = employeeRepository.findById(id)
        if (optionalEmployee.isPresent()) {
            return ResponseEntity.ok(optionalEmployee.get())
        } else {
            return ResponseEntity.notFound().build()
        }
    }

    @PostMapping
    def post(@RequestBody Employee employee, UriComponentsBuilder uri) {
        def id = employeeRepository.save(employee).id
        def path = uri.path("employees/${id}").build().toUri()
        return ResponseEntity.created(path).build()
    }

    @PutMapping("{id}")
    def put(@PathVariable long id, @RequestBody Employee employee) {
        def optionalEmployee = employeeRepository.findById(id)
        if (optionalEmployee.isPresent()) {
            def existingEmployee = optionalEmployee.get()
            existingEmployee.update(employee)
            employeeRepository.save(existingEmployee)
            return ResponseEntity.noContent().build()
        } else {
            return ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("{id}")
    def delete(@PathVariable long id) {
        def optionalEmployee = employeeRepository.findById(id)
        if (optionalEmployee.isPresent()) {
            employeeRepository.deleteById(id)
            return ResponseEntity.noContent().build()
        } else {
            return ResponseEntity.notFound().build()
        }
    }

}
