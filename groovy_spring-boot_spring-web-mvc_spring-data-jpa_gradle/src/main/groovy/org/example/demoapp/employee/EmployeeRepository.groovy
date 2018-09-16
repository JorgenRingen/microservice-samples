package org.example.demoapp.employee


import org.springframework.data.jpa.repository.JpaRepository

interface EmployeeRepository extends JpaRepository<Employee, Long> {

}