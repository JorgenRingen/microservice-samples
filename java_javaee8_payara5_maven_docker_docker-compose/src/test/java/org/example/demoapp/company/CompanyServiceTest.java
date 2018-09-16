package org.example.demoapp.company;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private CompanyService companyService;

    // Not much to test here, but providing junit+mockito example anyway
    @Test
    void findById() {
        long company = -42L;
        companyService.findById(company);
        verify(entityManager).find(Company.class, company);
    }
}