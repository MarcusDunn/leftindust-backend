package com.leftindust.mediq.dao

import com.leftindust.mediq.dao.entity.AccessControlList
import com.leftindust.mediq.extensions.CustomResult

interface AuthorizationDao {
    suspend fun getRolesForUserByUid(uid: String): CustomResult<List<AccessControlList>, OrmFailureReason>
}