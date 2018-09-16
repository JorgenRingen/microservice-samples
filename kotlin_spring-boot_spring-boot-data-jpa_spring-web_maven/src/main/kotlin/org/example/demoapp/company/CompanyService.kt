package org.example.demoapp.company

import org.example.demoapp.employee.EmployeeNotFoundException
import org.example.demoapp.employee.EmployeeService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional
class CompanyService(val companyRepository: CompanyRepository,
                     val employeeService: EmployeeService) {

    fun findAll(): List<Company> {
        return companyRepository.findAll()
    }

    fun findById(id: Long): Optional<Company> {
        return companyRepository.findById(id)
    }

    fun save(company: Company): Company {
        return companyRepository.save(company)
    }

    fun delete(id: Long) {
        companyRepository.findById(id)
                .ifPresent { company: Company -> companyRepository.delete(company) }
    }

    fun addEmployee(companyId: Long, employeeId: Long) {
        val company = companyRepository.findById(companyId)
                .orElseThrow { CompanyNotFoundException() }

        if (company.alreadeEmployed(employeeId)) {
            throw EmployeeAlreadyEmployedInCompanyException()
        }

        val employee = employeeService.findById(employeeId)
                .orElseThrow { EmployeeNotFoundException() }

        company.employees.add(employee)
    }

    fun removeEmployee(companyId: Long, employeeId: Long) {
        companyRepository.findById(companyId).ifPresent { company ->
            company.employees.removeIf { employee -> employee.id == employeeId }
        }
    }
}