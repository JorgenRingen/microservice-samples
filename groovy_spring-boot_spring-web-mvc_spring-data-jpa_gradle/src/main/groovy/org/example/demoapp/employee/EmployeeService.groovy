package org.example.demoapp.employee

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import javax.transaction.Transactional

@Service
@Transactional
class EmployeeService {

    @Autowired
    EmployeeRepository employeeRepository

    List<Employee> findAll() {
        return employeeRepository.findAll()
    }

    Optional<Employee> findById(long id) {
        return employeeRepository.findById(id)
    }

    Employee save(Employee employee) {
        return employeeRepository.save(employee)
    }

    void delete(long id) {
        employeeRepository.findById(id)
                .ifPresent({ employee -> employeeRepository.delete(employee) })
    }

    void updateEmployee(long id, Employee employee) {
        def existingEmployee = employeeRepository.findById(id)
                .orElseThrow({ return new EmployeeNotFoundException() })

        existingEmployee.updateFrom(employee)
    }
}
