package com.leftindust.mockingbird.dao

import com.google.gson.JsonObject
import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.dao.entity.MediqUser
import com.leftindust.mockingbird.extensions.CustomResult
import com.leftindust.mockingbird.graphql.types.input.GraphQLUserInput

interface UserDao {
    suspend fun getUserByUid(uid: String, requester: MediqToken): CustomResult<MediqUser, OrmFailureReason>
    suspend fun setUserSettingsByUid(
        uid: String,
        version: Int,
        settings: JsonObject,
        requester: MediqToken
    ): CustomResult<MediqUser, OrmFailureReason>

    suspend fun addUser(user: MediqUser, requester: MediqToken): CustomResult<MediqUser, OrmFailureReason>
    suspend fun addUser(user: GraphQLUserInput, requester: MediqToken): CustomResult<MediqUser, OrmFailureReason>
    suspend fun getUsers(from: Int, to: Int, requester: MediqToken): CustomResult<List<MediqUser>, OrmFailureReason>
}