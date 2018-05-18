package org.example.demoapp.resource;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.example.demoapp.entity.Employee;
import org.example.demoapp.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeResource(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @GetMapping
    public ResponseEntity<List<Employee>> findAll() {
        return ResponseEntity.ok(employeeRepository.findAll());
    }

    @GetMapping("{id}")
    public ResponseEntity<Employee> findById(@PathVariable final Long id) {
        final Optional<Employee> employee = employeeRepository.findById(id);
        return employee.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Long> post(@RequestBody final Employee employee, UriComponentsBuilder uri) {
        final Long id = employeeRepository.save(employee).getId();
        final URI path = uri.path(RESOURCE_BASE_URI + "/" + id).build().toUri();
        return ResponseEntity.created(path).build();
    }

    @PutMapping("{id}")
    public ResponseEntity put(@PathVariable final Long id, @RequestBody final Employee employee) {
        Optional<Employee> optionalEmployee = employeeRepository.findById(id);
        if (optionalEmployee.isPresent()) {
            Employee employeeToUpdate = optionalEmployee.get();
            Employee updatedEmployee = employeeToUpdate.update(employee);
            employeeRepository.save(updatedEmployee);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity delete(@PathVariable final Long id) {
        Optional<Employee> employeeToDelete = employeeRepository.findById(id);
        if (employeeToDelete.isPresent()) {
            employeeRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
