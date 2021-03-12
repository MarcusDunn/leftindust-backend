package com.leftindust.mockingbird.dao

import com.leftindust.mockingbird.dao.entity.AccessControlList
import com.leftindust.mockingbird.extensions.CustomResult

interface AuthorizationDao {
    suspend fun getRolesForUserByUid(uid: String): CustomResult<List<AccessControlList>, OrmFailureReason>
}