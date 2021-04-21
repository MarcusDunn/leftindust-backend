package com.leftindust.mockingbird.dao.impl

import com.expediagroup.graphql.generator.scalars.ID
import com.leftindust.mockingbird.auth.Authorizer
import com.leftindust.mockingbird.auth.Crud
import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.auth.NotAuthorizedException
import com.leftindust.mockingbird.dao.GroupDao
import com.leftindust.mockingbird.dao.Tables
import com.leftindust.mockingbird.dao.entity.MediqGroup
import com.leftindust.mockingbird.dao.impl.repository.HibernateGroupRepository
import com.leftindust.mockingbird.extensions.toLong
import com.leftindust.mockingbird.graphql.types.input.GraphQLGroupInput
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional
class GroupDaoImpl(
    @Autowired authorizer: Authorizer,
    @Autowired private val groupRepository: HibernateGroupRepository,
    ) : GroupDao, AbstractHibernateDao(authorizer) {
    override suspend fun addGroup(group: GraphQLGroupInput, requester: MediqToken): MediqGroup {
        if (requester can (Crud.CREATE to Tables.Group)) {
            val groupEntity = MediqGroup(group)
            return groupRepository.save(groupEntity)
        } else {
            throw NotAuthorizedException(requester, Crud.CREATE to Tables.Group)
        }
    }

    override suspend fun getAllGroups(requester: MediqToken): Collection<MediqGroup> {
        if (requester can (Crud.READ to Tables.Group)) {
            return groupRepository.findAll()
        } else {
            throw NotAuthorizedException(requester, Crud.READ to Tables.Group)
        }
    }

    override suspend fun getGroupById(gid: ID, requester: MediqToken): MediqGroup {
        if (requester can (Crud.READ to Tables.Group)) {
            return groupRepository.getOne(gid.toLong())
        } else {
            throw NotAuthorizedException(requester, Crud.READ to Tables.Group)
        }
    }

}