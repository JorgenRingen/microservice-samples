package org.example.demoapp.entity

import javax.persistence.*

@Entity
data class Company(
        
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long? = null,
        var name: String?  = null,
        @OneToMany 
        @JoinTable(name = "company_employees",
            joinColumns = [(JoinColumn(name = "company_id"))],
            inverseJoinColumns = [(JoinColumn(name = "employee_id"))])
        var employees: MutableSet<Employee>  = mutableSetOf()
)
