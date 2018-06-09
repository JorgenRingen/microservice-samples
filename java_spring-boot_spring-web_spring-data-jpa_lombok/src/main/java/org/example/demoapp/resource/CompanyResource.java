package org.example.demoapp.resource;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.example.demoapp.entity.Company;
import org.example.demoapp.entity.Employee;
import org.example.demoapp.repository.CompanyRepository;
import org.example.demoapp.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping(CompanyResource.RESOURCE_BASE_URI)
public class CompanyResource {

    static final String RESOURCE_BASE_URI = "companies";

    private final CompanyRepository companyRepository;
    private final EmployeeRepository employeeRepository;

    @Autowired
    public CompanyResource(CompanyRepository companyRepository,
                           EmployeeRepository employeeRepository) {
        this.companyRepository = companyRepository;
        this.employeeRepository = employeeRepository;
    }

    @GetMapping
    public ResponseEntity<List<Company>> findAll() {
        return ResponseEntity.ok(companyRepository.findAll());
    }

    @GetMapping("{id}")
    public ResponseEntity<Company> findById(@PathVariable long id) {
        Optional<Company> company = companyRepository.findById(id);
        return company.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Long> post(@RequestBody Company company, UriComponentsBuilder uri) {
        long id = companyRepository.save(company).getId();
        URI path = uri.path(RESOURCE_BASE_URI + "/" + id).build().toUri();
        return ResponseEntity.created(path).build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity delete(@PathVariable long id) {
        Optional<Company> companyToDelete = companyRepository.findById(id);
        if (companyToDelete.isPresent()) {
            employeeRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("{companyId}/employees")
    public ResponseEntity addEmployee(@PathVariable("companyId") long companyId, @RequestBody long employeeId) {
        Optional<Company> optionalCompany = companyRepository.findById(companyId);
        if (!optionalCompany.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Optional<Employee> optionalEmployee = employeeRepository.findById(employeeId);
        if (!optionalEmployee.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Company company = optionalCompany.get();
        boolean employeeAlreadyEmployed = company.isEmployeeEmployed(employeeId);

        if (employeeAlreadyEmployed) {
            return ResponseEntity.badRequest().body("Employee with id=" + employeeId + " already in company with id=" + companyId);
        } else {
            company.addEmployee(optionalEmployee.get());
            companyRepository.save(company);
            return ResponseEntity.noContent().build();
        }
    }

    @DeleteMapping("{companyId}/employees/{employeeId}")
    public ResponseEntity removeEmployee(@PathVariable("companyId") long companyId, @PathVariable("employeeId") long employeeId) {
        Optional<Company> optionalCompany = companyRepository.findById(companyId);
        if (!optionalCompany.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Optional<Employee> optionalEmployee = employeeRepository.findById(employeeId);
        if (!optionalEmployee.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Company company = optionalCompany.get();
        company.removeEmployee(optionalEmployee.get());
        companyRepository.save(company);

        return ResponseEntity.noContent().build();
    }
}

