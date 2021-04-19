package com.leftindust.mockingbird.dao

import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.dao.entity.EmergencyContact
import com.leftindust.mockingbird.extensions.CustomResult

interface ContactDao {
    suspend fun getByPatient(pid: Long, requester: MediqToken): Collection<EmergencyContact>
}
