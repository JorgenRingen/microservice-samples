package org.example.demoapp

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class MyDemoApplication

fun main(args: Array<String>) {
    SpringApplication.run(MyDemoApplication::class.java, *args)
}


