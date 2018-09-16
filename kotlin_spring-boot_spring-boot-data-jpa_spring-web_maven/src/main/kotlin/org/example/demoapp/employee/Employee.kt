package org.example.demoapp.employee

import java.time.LocalDate
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
data class Employee(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long? = null,
        var firstname: String? = null,
        var lastname: String? = null,
        var dateOfBirth: LocalDate? = null) {

    fun updateFrom(updatedEmployee: Employee) {
        this.firstname = updatedEmployee.firstname
        this.lastname = updatedEmployee.lastname
        this.dateOfBirth = updatedEmployee.dateOfBirth
    }
}