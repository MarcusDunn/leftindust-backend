package com.leftindust.mockingbird.dao.impl

import com.expediagroup.graphql.generator.scalars.ID
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
import com.leftindust.mockingbird.extensions.toLong
import com.leftindust.mockingbird.graphql.types.input.GraphQLPermissionInput
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
    override suspend fun addUserPermission(
        uid: String,
        permission: GraphQLPermissionInput,
        requester: MediqToken
    ): AccessControlList {
        val createAcl = Crud.CREATE to Tables.AccessControlList
        return if (requester can createAcl) {
            val user = userRepository.getByUniqueId(uid);
            val action = Action(permission)
            val acl = AccessControlList(mediqUser = user, action = action)
            aclRepository.save(acl)
        } else {
            throw NotAuthorizedException(requester, createAcl)
        }
    }

    override suspend fun addGroupPermission(
        gid: ID,
        permission: GraphQLPermissionInput,
        requester: MediqToken
    ): AccessControlList {
        val createAcl = Crud.CREATE to Tables.AccessControlList
        return if (requester can createAcl) {
            val group = groupRepository.getOne(gid.toLong())
            val action = Action(permission)
            val acl = AccessControlList(group = group, action = action)
            aclRepository.save(acl)
        } else {
            throw NotAuthorizedException(requester, createAcl)
        }
    }
}