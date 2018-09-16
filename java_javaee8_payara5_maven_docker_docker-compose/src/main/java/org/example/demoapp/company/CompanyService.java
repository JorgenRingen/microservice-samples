package org.example.demoapp.company;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;
import java.util.Optional;

import org.example.demoapp.employee.Employee;
import org.example.demoapp.employee.EmployeeNotFoundException;
import org.example.demoapp.employee.EmployeeService;

@Stateless
public class CompanyService {

    @PersistenceContext
    private EntityManager em;

    @Inject
    private EmployeeService employeeService;

    public List<Company> findAll() {
        return em.createQuery("select c from Company c").getResultList();
    }

    public Optional<Company> findById(long id) {
        return Optional.ofNullable(em.find(Company.class, id));
    }

    public Company save(Company employee) {
        return em.merge(employee);
    }

    public void delete(long id) {
        findById(id)
                .ifPresent(em::remove);
    }

    public void addEmployee(long companyId, long employeeId) {
        Company company = findById(companyId)
                .orElseThrow(CompanyNotFoundException::new);

        if (company.isEmployed(employeeId)) {
            throw new EmployeeAlreadyEmployedInCompanyException(companyId, employeeId);
        }

        Employee employee = employeeService.findById(employeeId)
                .orElseThrow(EmployeeNotFoundException::new);

        company.addEmployee(employee);
    }

    public void removeEmployee(long companyId, long employeeId) {
        findById(companyId)
                .ifPresent(company -> company.removeEmployee(employeeId));
    }
}
