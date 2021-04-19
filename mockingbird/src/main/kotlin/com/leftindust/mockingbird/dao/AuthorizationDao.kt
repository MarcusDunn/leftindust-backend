package com.leftindust.mockingbird.dao

import com.leftindust.mockingbird.dao.entity.AccessControlList
import com.leftindust.mockingbird.extensions.CustomResult

interface AuthorizationDao {
    suspend fun getRolesForUserByUid(uid: String): List<AccessControlList>
    suspend fun isAdmin(uid: String): Boolean
}