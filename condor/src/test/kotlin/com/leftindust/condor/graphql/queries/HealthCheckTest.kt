package com.leftindust.condor.graphql.queries

import com.leftindust.condor.graphql.types.CondorStatus
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import javax.sql.DataSource

@SpringBootTest
internal class HealthQueryTest(
    @Autowired private val healthQuery: HealthQuery
) {

    @Test
    fun condorIsAlive(@Autowired dataSource: DataSource) {
        val result = healthQuery.condorIsAlive(dataSource)

        assertEquals(
            CondorStatus(
                isAlive = true,
                connectedToDatabase = true,
            ),
            result
        )
    }
}