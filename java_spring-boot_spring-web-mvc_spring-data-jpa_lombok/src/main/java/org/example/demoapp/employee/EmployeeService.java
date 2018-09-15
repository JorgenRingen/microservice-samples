package org.example.demoapp.service;

import javax.transaction.Transactional;

import java.util.List;
import java.util.Optional;

import org.example.demoapp.entity.Employee;
import org.example.demoapp.repository.EmployeeRepository;
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

    public Employee save(Employee employee) {
        return employeeRepository.save(employee);
    }

    public void deleteById(long id) {
        employeeRepository.findById(id)
                .ifPresent(employeeRepository::delete);
    }

    public void updateEmployee(long id, Employee employee) {
        Employee existingEmployee = employeeRepository.findById(id)
                .orElseThrow(EmployeeNotFoundException::new);
        existingEmployee.updateFrom(employee);
    }
}
