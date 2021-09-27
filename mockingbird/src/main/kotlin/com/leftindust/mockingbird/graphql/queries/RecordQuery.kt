package com.leftindust.mockingbird.graphql.queries

import com.expediagroup.graphql.generator.exceptions.GraphQLKotlinException
import com.expediagroup.graphql.server.operations.Query
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.RecordDao
import com.leftindust.mockingbird.graphql.types.GraphQLPatient
import com.leftindust.mockingbird.graphql.types.GraphQLRecord
import org.springframework.stereotype.Component

@Component
class RecordQuery(
    private val recordDao: RecordDao,
) : Query {
    suspend fun getRecords(
        pid: GraphQLPatient.ID? = null,
        rids: List<GraphQLRecord.ID>? = null,
        authContext: GraphQLAuthContext
    ): List<GraphQLRecord> {
        val requester = authContext.mediqAuthToken
        return when {
            pid == null && rids != null -> {
                rids.map { recordDao.getRecordByRecordId(it, requester) }.map { GraphQLRecord(it, authContext) }
            }
            pid != null && rids == null -> {
                val records = recordDao.getRecordsByPatientPid(pid, requester)
                return records.map { GraphQLRecord(it, authContext) }
            }
            else -> throw GraphQLKotlinException("invalid argument combination to getRecords")
        }
    }
}
