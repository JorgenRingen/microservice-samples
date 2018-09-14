package org.example.demoapp.service;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;
import java.util.Optional;

import org.example.demoapp.entity.Company;

@Stateless
public class CompanyService {

    @PersistenceContext
    private EntityManager em;

    public List<Company> findAll() {
        return em.createQuery("select c from Company c").getResultList();
    }

    public Optional<Company> findById(long id) {
        return Optional.ofNullable(em.find(Company.class, id));
    }

    public Company save(Company employee) {
        return em.merge(employee);
    }

    public void delete(Company company) {
        em.remove(em.merge(company));
    }
}
