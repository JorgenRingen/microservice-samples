package org.example.demoapp.employee

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

import java.time.LocalDate

@Entity
class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id
    String firstname
    String lastname
    LocalDate dateOfBirth

    void updateFrom(Employee employee) {
        firstname = employee.firstname
        lastname = employee.lastname
        dateOfBirth = employee.dateOfBirth
    }
}
