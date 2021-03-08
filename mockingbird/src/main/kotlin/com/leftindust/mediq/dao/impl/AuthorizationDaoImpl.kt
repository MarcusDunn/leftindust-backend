package com.leftindust.mediq.dao.impl

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserRecord
import com.leftindust.mediq.auth.Crud
import com.leftindust.mediq.auth.MediqToken
import com.leftindust.mediq.dao.*
import com.leftindust.mediq.dao.entity.AccessControlList
import com.leftindust.mediq.dao.impl.repository.HibernateAclRepository
import com.leftindust.mediq.dao.impl.repository.HibernateUserRepository
import com.leftindust.mediq.extensions.CustomResult
import com.leftindust.mediq.extensions.Failure
import com.leftindust.mediq.extensions.Success
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Transactional
@Repository
class AuthorizationDaoImpl(
    private val aclRepository: HibernateAclRepository,
    private val userRepository: HibernateUserRepository
) : AuthorizationDao {
    override suspend fun getRolesForUserByUid(uid: String): CustomResult<List<AccessControlList>, OrmFailureReason> {
        val user = userRepository.getUserByUniqueId(uid) ?: return Failure(DoesNotExist())
        val userPerms = aclRepository.findAllByMediqUser(user)
        val groupPerms = user.group?.let { aclRepository.findAllByGroup(it) } ?: emptyList()
        return Success(userPerms + groupPerms)
    }
}