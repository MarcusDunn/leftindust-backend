package com.leftindust.mockingbird.dao

import com.leftindust.mockingbird.dao.entity.AccessControlList

interface AuthorizationDao {
    fun getRolesForUserByUid(uid: String): List<AccessControlList>
    fun isAdmin(uid: String): Boolean
    fun isPatient(uid: String): Boolean
}