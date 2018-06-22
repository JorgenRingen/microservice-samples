package org.example.demoapp.repository

import org.example.demoapp.entity.Company
import org.springframework.data.jpa.repository.JpaRepository

interface CompanyRepository extends JpaRepository<Company, Long> {

}