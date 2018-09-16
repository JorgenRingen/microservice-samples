package org.example.demoapp.employee

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional
class EmployeeService(val employeeRepository: EmployeeRepository) {

    fun findAll(): List<Employee> {
        return employeeRepository.findAll()
    }

    fun findById(id: Long): Optional<Employee> {
        return employeeRepository.findById(id)
    }

    fun save(employee: Employee): Employee {
        return employeeRepository.save(employee)
    }

    fun updateEmployee(id: Long, updatedEmployee: Employee) {
        val employee = employeeRepository.findById(id)
                .orElseThrow { EmployeeNotFoundException() }
        employee.updateFrom(updatedEmployee)
    }

    fun delete(id: Long) {
        employeeRepository.findById(id)
                .ifPresent { employee: Employee -> employeeRepository.delete(employee) }
    }

}