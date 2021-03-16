package com.leftindust.condor.graphql.queries

import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.expediagroup.graphql.types.operations.Query
import com.leftindust.condor.graphql.types.CondorStatus
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
}