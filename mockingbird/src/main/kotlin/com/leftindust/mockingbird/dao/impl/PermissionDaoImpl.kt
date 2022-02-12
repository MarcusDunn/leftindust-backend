package com.leftindust.mockingbird.dao.impl

import com.leftindust.mockingbird.auth.Authorizer
import com.leftindust.mockingbird.auth.Crud
import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.auth.NotAuthorizedException
import com.leftindust.mockingbird.dao.PermissionDao
import com.leftindust.mockingbird.dao.Tables
import com.leftindust.mockingbird.dao.entity.AccessControlList
import com.leftindust.mockingbird.dao.entity.Action
import com.leftindust.mockingbird.dao.impl.repository.HibernateAclRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernateGroupRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernateUserRepository
import com.leftindust.mockingbird.graphql.types.GraphQLUser
import com.leftindust.mockingbird.graphql.types.input.GraphQLPermissionInput
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Repository
import javax.transaction.Transactional

@Transactional
@Repository
class PermissionDaoImpl(
    private val groupRepository: HibernateGroupRepository,
    private val aclRepository: HibernateAclRepository,
    private val userRepository: HibernateUserRepository,
    authorizer: Authorizer,
) : PermissionDao, AbstractHibernateDao(authorizer) {
    companion object {
        private val createAcl = Crud.CREATE to Tables.AccessControlList
    }

    override suspend fun addUserPermission(
        uid: String,
        permission: GraphQLPermissionInput,
        requester: MediqToken
    ): AccessControlList = if (requester can createAcl) withContext(Dispatchers.IO) {
        val user = userRepository.getByUniqueId(uid)
        val action = Action(permission)
        val acl = AccessControlList(mediqUser = user, action = action)
        aclRepository.save(acl)
    } else {
        throw NotAuthorizedException(requester, createAcl)
    }

    override suspend fun addGroupPermission(
        gid: GraphQLUser.Group.ID,
        permission: GraphQLPermissionInput,
        requester: MediqToken
    ): AccessControlList = if (requester can createAcl) withContext(Dispatchers.IO) {
        val group = groupRepository.getById(gid.id)
        val action = Action(permission)
        val acl = AccessControlList(group = group, action = action)
        aclRepository.save(acl)
    } else {
        throw NotAuthorizedException(requester, createAcl)
    }
}