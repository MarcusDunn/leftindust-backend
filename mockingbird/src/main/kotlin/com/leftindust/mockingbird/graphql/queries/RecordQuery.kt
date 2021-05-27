package com.leftindust.mockingbird.graphql.queries

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
    suspend fun getRecord(rid: GraphQLRecord.ID, authContext: GraphQLAuthContext): GraphQLRecord {
        val requester = authContext.mediqAuthToken
        val record = recordDao.getRecordByRecordId(rid, requester)
        return GraphQLRecord(record, authContext)
    }

    suspend fun getRecords(pid: GraphQLPatient.ID, authContext: GraphQLAuthContext): List<GraphQLRecord> {
        val requester = authContext.mediqAuthToken
        val record = recordDao.getRecordsByPatientPid(pid, requester)
        return record.map { GraphQLRecord(it, authContext) }
    }
}
