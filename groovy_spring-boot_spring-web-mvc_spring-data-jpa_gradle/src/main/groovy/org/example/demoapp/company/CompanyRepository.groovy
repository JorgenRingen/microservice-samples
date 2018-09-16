package org.example.demoapp.company


import org.springframework.data.jpa.repository.JpaRepository

interface CompanyRepository extends JpaRepository<Company, Long> {

}