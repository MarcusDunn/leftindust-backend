package com.leftindust.mockingbird.dao

import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.dao.entity.MediqRecord
import com.leftindust.mockingbird.graphql.types.GraphQLPatient
import com.leftindust.mockingbird.graphql.types.GraphQLRecord
import com.leftindust.mockingbird.graphql.types.input.GraphQLRecordInput

interface RecordDao {
    suspend fun getRecordByRecordId(rid: GraphQLRecord.ID, requester: MediqToken): MediqRecord
    suspend fun getRecordsByPatientPid(pid: GraphQLPatient.ID, requester: MediqToken): Collection<MediqRecord>
    suspend fun addRecord(record: GraphQLRecordInput, requester: MediqToken): MediqRecord {
        TODO("Not yet implemented")
    }
}
