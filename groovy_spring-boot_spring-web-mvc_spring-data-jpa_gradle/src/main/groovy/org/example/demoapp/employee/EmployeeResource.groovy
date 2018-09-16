package org.example.demoapp.employee


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
    EmployeeService employeeService

    @GetMapping
    def findAll() {
        return ResponseEntity.ok(employeeService.findAll())
    }

    @GetMapping("{id}")
    def findById(@PathVariable long id) {
        def optionalEmployee = employeeService.findById(id)
        if (optionalEmployee.isPresent()) {
            return ResponseEntity.ok(optionalEmployee.get())
        } else {
            return ResponseEntity.notFound().build()
        }
    }

    @PostMapping
    def post(@RequestBody Employee employee, UriComponentsBuilder uri) {
        def id = employeeService.save(employee).id
        def path = uri.path("employees/${id}").build().toUri()
        return ResponseEntity.created(path).build()
    }

    @PutMapping("{id}")
    def put(@PathVariable long id, @RequestBody Employee employee) {
        try {
            employeeService.updateEmployee(id, employee)
        } catch (EmployeeNotFoundException ignored) {
            return ResponseEntity.notFound().build()
        }

        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("{id}")
    def delete(@PathVariable long id) {
        employeeService.delete(id)
        return ResponseEntity.noContent().build()
    }

}
