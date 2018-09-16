package org.example.demoapp.employee;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;
import java.util.Optional;

@Stateless
public class EmployeeService {

    @PersistenceContext
    private EntityManager em;

    public List<Employee> findAll() {
        return em.createQuery("select e from Employee e").getResultList();
    }

    public Optional<Employee> findById(long id) {
        return Optional.ofNullable(em.find(Employee.class, id));
    }

    public Employee save(Employee employee) {
        return em.merge(employee);
    }

    public void delete(long id) {
        findById(id)
                .ifPresent(em::remove);
    }

    public void updateEmployee(long id, Employee employee) {
        Employee existingEmployee = findById(id)
                .orElseThrow(EmployeeNotFoundException::new);
        existingEmployee.updateFrom(employee);
    }
}
