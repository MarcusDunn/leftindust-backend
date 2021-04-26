package com.leftindust.mockingbird.dao

import com.expediagroup.graphql.generator.scalars.ID
import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.dao.entity.MediqUser
import com.leftindust.mockingbird.extensions.CustomResult
import com.leftindust.mockingbird.graphql.types.input.GraphQLRangeInput
import com.leftindust.mockingbird.graphql.types.input.GraphQLUserEditInput
import com.leftindust.mockingbird.graphql.types.input.GraphQLUserInput

interface UserDao {
    suspend fun getUserByUid(uid: String, requester: MediqToken): CustomResult<MediqUser, OrmFailureReason>
    suspend fun addUser(user: GraphQLUserInput, requester: MediqToken): MediqUser
    suspend fun getUsers(range: GraphQLRangeInput, requester: MediqToken): Collection<MediqUser>
    suspend fun updateUser(user: GraphQLUserEditInput, requester: MediqToken): MediqUser
    suspend fun getByDoctor(did: ID , requester: MediqToken): MediqUser?
}