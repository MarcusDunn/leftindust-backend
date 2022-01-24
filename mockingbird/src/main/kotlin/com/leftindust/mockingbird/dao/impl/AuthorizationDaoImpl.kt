package com.leftindust.mockingbird.dao.impl

import com.leftindust.mockingbird.dao.AuthorizationDao
import com.leftindust.mockingbird.dao.entity.AccessControlList
import com.leftindust.mockingbird.dao.impl.repository.HibernateAclRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernateUserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Transactional
@Repository
class AuthorizationDaoImpl(
    private val aclRepository: HibernateAclRepository,
    private val userRepository: HibernateUserRepository
) : AuthorizationDao {
    override suspend fun getRolesForUserByUid(uid: String): List<AccessControlList> = withContext(Dispatchers.IO) {
        val user = userRepository.findByUniqueId(uid) ?: return@withContext emptyList()
        val userPerms = aclRepository.findAllByMediqUser(user)
        val groupPerms = user.group?.let { aclRepository.findAllByGroup(it) } ?: emptyList()
        userPerms + groupPerms
    }

    override suspend fun isAdmin(uid: String): Boolean = withContext(Dispatchers.IO) {
        adminAlias.contains(userRepository.findByUniqueId(uid)?.group?.name)
    }

    override suspend fun isPatient(uid: String): Boolean = withContext(Dispatchers.IO) {
        patientAlias.contains(userRepository.findByUniqueId(uid)?.group?.name)
    }

    val adminAlias = listOf("admin", "Administrator")
    val patientAlias = listOf("patient", "Patient")
}