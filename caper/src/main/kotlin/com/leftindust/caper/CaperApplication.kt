package com.leftindust.caper

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CaperApplication

fun main(args: Array<String>) {
	runApplication<CaperApplication>(*args)
}
