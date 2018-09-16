package org.example.demoapp.company

import org.example.demoapp.employee.Employee
import javax.persistence.*

@Entity
data class Company(

        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long? = null,
        var name: String? = null,
        @OneToMany
        @JoinTable(name = "company_employees",
                joinColumns = [(JoinColumn(name = "company_id"))],
                inverseJoinColumns = [(JoinColumn(name = "employee_id"))])
        var employees: MutableSet<Employee> = mutableSetOf()) {

    fun alreadeEmployed(employeeId: Long): Boolean {
        return employees.any { employeeId == employeeId }
    }
}
