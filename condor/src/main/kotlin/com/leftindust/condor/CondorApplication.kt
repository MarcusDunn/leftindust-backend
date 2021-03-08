package com.leftindust.condor

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CondorApplication

fun main(args: Array<String>) {
    runApplication<CondorApplication>(*args)
}
