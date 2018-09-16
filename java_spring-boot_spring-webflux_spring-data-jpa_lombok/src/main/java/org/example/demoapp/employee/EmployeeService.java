package org.example.demoapp.employee;

import reactor.core.publisher.Mono;

import javax.transaction.Transactional;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
@Transactional
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    public Optional<Employee> findById(long id) {
        return employeeRepository.findById(id);
    }

    public Employee save(Mono<Employee> employeeMono) {
        return employeeRepository.save(employeeMono.toProcessor().block()); // needs to block
    }

    public void deleteById(long id) {
        employeeRepository.findById(id)
                .ifPresent(employeeRepository::delete);
    }

    public void updateEmployee(long id, Mono<Employee> employeeMono) {
        Employee existingEmployee = employeeRepository.findById(id)
                .orElseThrow(EmployeeNotFoundException::new);

        existingEmployee.updateFrom(employeeMono.toProcessor().block()); // needs to block
    }
}
