package org.example.demoapp.service;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;
import java.util.Optional;

import org.example.demoapp.entity.Employee;

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

    public void delete(Employee employee) {
        em.remove(em.merge(employee));
    }
}
