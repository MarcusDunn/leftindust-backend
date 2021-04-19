package com.leftindust.mockingbird.dao.impl

import com.leftindust.mockingbird.dao.AuthorizationDao
import com.leftindust.mockingbird.dao.DoesNotExist
import com.leftindust.mockingbird.dao.entity.AccessControlList
import com.leftindust.mockingbird.dao.impl.repository.HibernateAclRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernateUserRepository
import com.leftindust.mockingbird.extensions.Failure
import com.leftindust.mockingbird.extensions.Success
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Transactional
@Repository
class AuthorizationDaoImpl(
    private val aclRepository: HibernateAclRepository,
    private val userRepository: HibernateUserRepository
) : AuthorizationDao {
    override suspend fun getRolesForUserByUid(uid: String): List<AccessControlList> {
        val user = userRepository.getUserByUniqueId(uid)
        val userPerms = aclRepository.findAllByMediqUser(user)
        val groupPerms = user.group?.let { aclRepository.findAllByGroup(it) } ?: emptyList()
        return userPerms + groupPerms
    }

    override suspend fun isAdmin(uid: String): Boolean {
        return userRepository.getUserByUniqueId(uid).group?.name == "admin"
    }
}