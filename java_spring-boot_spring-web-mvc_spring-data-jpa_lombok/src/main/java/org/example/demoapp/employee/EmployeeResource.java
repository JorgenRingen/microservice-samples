package org.example.demoapp.resource;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.example.demoapp.entity.Employee;
import org.example.demoapp.service.EmployeeNotFoundException;
import org.example.demoapp.service.EmployeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping(EmployeeResource.RESOURCE_BASE_URI)
public class EmployeeResource {

    static final String RESOURCE_BASE_URI = "employees";

    private final EmployeeService employeeService;

    public EmployeeResource(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public ResponseEntity<List<Employee>> findAll() {
        return ResponseEntity.ok(employeeService.findAll());
    }

    @GetMapping("{id}")
    public ResponseEntity<Employee> findById(@PathVariable long id) {
        Optional<Employee> employee = employeeService.findById(id);
        return employee.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Long> post(@RequestBody Employee employee, UriComponentsBuilder uri) {
        long id = employeeService.save(employee).getId();
        URI path = uri.path(RESOURCE_BASE_URI + "/" + id).build().toUri();
        return ResponseEntity.created(path).build();
    }

    @PutMapping("{id}")
    public ResponseEntity put(@PathVariable long id, @RequestBody Employee employee) {
        try {
            employeeService.updateEmployee(id, employee);
        } catch (EmployeeNotFoundException e) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity delete(@PathVariable long id) {
        employeeService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
