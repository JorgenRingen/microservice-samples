package org.example.demoapp.service;

import javax.transaction.Transactional;

import java.util.List;
import java.util.Optional;

import org.example.demoapp.entity.Company;
import org.example.demoapp.repository.CompanyRepository;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public List<Company> findAll() {
        return companyRepository.findAll();
    }

    public Optional<Company> findById(long id) {
        return companyRepository.findById(id);
    }

    public Company save(Company company) {
        return companyRepository.save(company);
    }

    public void delete(long id) {
        companyRepository.findById(id)
                .ifPresent(companyRepository::delete);
    }
}
