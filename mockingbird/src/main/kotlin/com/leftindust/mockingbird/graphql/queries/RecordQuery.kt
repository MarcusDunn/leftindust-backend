package com.leftindust.mockingbird.graphql.queries

import com.expediagroup.graphql.scalars.ID
import com.expediagroup.graphql.server.operations.Query
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.RecordDao
import com.leftindust.mockingbird.extensions.toInt
import com.leftindust.mockingbird.extensions.toLong
import com.leftindust.mockingbird.graphql.types.GraphQLRecord
import org.springframework.stereotype.Component

@Component
class RecordQuery(
    private val recordDao: RecordDao,
) : Query {
    suspend fun getRecord(record_id: ID, authContext: GraphQLAuthContext): GraphQLRecord {
        val requester = authContext.mediqAuthToken
        val record = recordDao
            .getRecordByRecordId(record_id.toInt(), requester)
            .getOrThrow()

        return GraphQLRecord(record, authContext)
    }

    suspend fun getRecords(pid: ID, authContext: GraphQLAuthContext): List<GraphQLRecord> {
        val requester = authContext.mediqAuthToken
        return recordDao
            .getRecordsByPatientPid(pid.toLong(), requester)
            .getOrThrow()
            .map { GraphQLRecord(it, authContext) }
    }
}
