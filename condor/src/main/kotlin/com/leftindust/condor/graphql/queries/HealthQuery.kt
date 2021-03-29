package com.leftindust.condor.graphql.queries

import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.expediagroup.graphql.server.operations.Query
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import javax.sql.DataSource

@Component
class HealthQuery : Query {
    fun condorIsAlive(@GraphQLIgnore @Autowired dataSource: DataSource): CondorStatus {
        return CondorStatus(
            isAlive = true,
            connectedToDatabase = try {
                dataSource.connection.isValid(5)
            } catch (e: Throwable) {
                false
            }
        )
    }

    data class CondorStatus(val isAlive: Boolean, val connectedToDatabase: Boolean)
}