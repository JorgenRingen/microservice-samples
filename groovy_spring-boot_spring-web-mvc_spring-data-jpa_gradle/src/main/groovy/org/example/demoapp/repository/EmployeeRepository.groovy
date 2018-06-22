package org.example.demoapp.repository

import org.example.demoapp.entity.Employee
import org.springframework.data.jpa.repository.JpaRepository

interface EmployeeRepository extends JpaRepository<Employee, Long> {

}