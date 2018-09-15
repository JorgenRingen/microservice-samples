package org.example.demoapp.entity

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany

@Entity
class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id

    String name

    @OneToMany
    @JoinTable(name = "company_employees",
            joinColumns = @JoinColumn(name = "company_id"),
            inverseJoinColumns = @JoinColumn(name = "employee_id"))
    Set<Employee> employees

    def isEmployeeEmployed(long id) {
        return employees.stream()
                .any { employee -> employee.id == id }
    }

    def addEmployee(Employee employee) {
        employees.add(employee)
    }

    def removeEmployee(Employee employee) {
        employees.removeIf({
            e -> e.id == employee.id
        })
    }
}
