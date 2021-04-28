package com.leftindust.mockingbird.dao

import com.expediagroup.graphql.generator.scalars.ID
import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.dao.entity.NameInfo

interface NameInfoDao {
    suspend fun getByUniqueId(uid: String, requester: MediqToken): NameInfo
}