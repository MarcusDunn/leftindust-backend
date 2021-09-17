package com.leftindust.mockingbird.dao

import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.dao.entity.MediqUser
import com.leftindust.mockingbird.graphql.types.GraphQLDoctor
import com.leftindust.mockingbird.graphql.types.GraphQLPatient
import com.leftindust.mockingbird.graphql.types.input.GraphQLRangeInput
import com.leftindust.mockingbird.graphql.types.input.GraphQLUserEditInput
import com.leftindust.mockingbird.graphql.types.input.GraphQLUserInput

interface UserDao {
    suspend fun findUserByUid(uid: String, requester: MediqToken): MediqUser?
    suspend fun getUserByUid(uid: String, requester: MediqToken): MediqUser
    suspend fun addUser(user: GraphQLUserInput, requester: MediqToken): MediqUser
    suspend fun getUsers(range: GraphQLRangeInput, requester: MediqToken): Collection<MediqUser>
    suspend fun updateUser(user: GraphQLUserEditInput, requester: MediqToken): MediqUser
    suspend fun findByDoctor(did: GraphQLDoctor.ID, requester: MediqToken): MediqUser?
    suspend fun findByPatient(pid: GraphQLPatient.ID, requester: MediqToken): MediqUser? {
        TODO("Not yet implemented")
    }
}