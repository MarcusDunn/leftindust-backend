package com.leftindust.mockingbird.dao

import com.leftindust.mockingbird.dao.entity.AccessControlList

interface AuthorizationDao {
    suspend fun getRolesForUserByUid(uid: String): List<AccessControlList>
    suspend fun isAdmin(uid: String): Boolean
}