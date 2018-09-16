package org.example.demoapp.company

import org.example.demoapp.employee.EmployeeNotFoundException
import org.example.demoapp.employee.EmployeeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import javax.transaction.Transactional

@Service
@Transactional
class CompanyService {

    @Autowired
    CompanyRepository companyRepository

    @Autowired
    EmployeeService employeeService

    List<Company> findAll() {
        return companyRepository.findAll()
    }

    Optional<Company> findById(long id) {
        return companyRepository.findById(id)
    }

    Company save(Company company) {
        return companyRepository.save(company)
    }

    void delete(long id) {
        companyRepository.findById(id)
                .ifPresent({ company -> companyRepository.delete(company) })
    }

    void addEmployee(long companyId, long employeeId) {
        def company = companyRepository.findById(companyId)
                .orElseThrow({ return new CompanyNotFoundException() })

        if (company.isEmployeeEmployed(employeeId)) {
            throw new EmployeeAlreadeEmployedInCompanyException()
        }

        def employee = employeeService.findById(employeeId)
                .orElseThrow({ return new EmployeeNotFoundException() })

        company.addEmployee(employee)
    }

    void removeEmployee(long companyId, long employeeId) {
        companyRepository.findById(companyId).ifPresent({ company ->
            company.removeEmployee(employeeId)
        })
    }
}
