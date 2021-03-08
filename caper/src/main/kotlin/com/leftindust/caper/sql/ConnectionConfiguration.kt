package com.leftindust.caper.sql

import org.jetbrains.exposed.sql.Database
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
class ConnectionConfiguration {
    companion object {
        private const val URL =
            "jdbc:postgresql:///postgres?cloudSqlInstance=leftindust:us-west1:caper-postgres&socketFactory=com.google.cloud.sql.postgres.SocketFactory"
        private const val DRIVER_CLASS_NAME = "org.postgresql.Driver"
        private const val USERNAME = "postgres"
        private const val PASSWORD = "leftindust"
    }

    @Bean
    fun datasource(): DataSource {
        return DataSourceBuilder.create()
            .driverClassName(DRIVER_CLASS_NAME)
            .password(PASSWORD)
            .url(URL)
            .username(USERNAME)
            .build()
    }

    @Bean
    fun database(dataSource: DataSource): Database {
        return Database.connect(dataSource)
    }
}