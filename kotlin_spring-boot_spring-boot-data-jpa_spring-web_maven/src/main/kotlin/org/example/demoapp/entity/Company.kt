package org.example.demoapp.entity

import javax.persistence.*

@Entity
data class Company(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long? = null,
        var name: String?  = null,
        @OneToMany var employees: MutableSet<Employee>  = mutableSetOf()
)
