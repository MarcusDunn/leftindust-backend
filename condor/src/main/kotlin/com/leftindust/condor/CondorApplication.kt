package com.leftindust.condor


import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication


@SpringBootApplication
@EnableConfigurationProperties
class MockingbirdApplication

/**
 * Entry point into the application, starts doing the spring magic
 * @param args command line arguments
 */
fun main(args: Array<String>) {
    runApplication<MockingbirdApplication>(*args)
}