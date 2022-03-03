package com.leftindust.mockingbird.dao.impl

import com.leftindust.mockingbird.auth.Authorizer
import com.leftindust.mockingbird.auth.Crud
import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.auth.NotAuthorizedException
import com.leftindust.mockingbird.dao.GroupDao
import com.leftindust.mockingbird.dao.Tables
import com.leftindust.mockingbird.dao.entity.MediqGroup
import com.leftindust.mockingbird.dao.impl.repository.HibernateGroupRepository
import com.leftindust.mockingbird.graphql.types.GraphQLUserGroup
import com.leftindust.mockingbird.graphql.types.input.GraphQLGroupInput
import com.leftindust.mockingbird.graphql.types.input.GraphQLRangeInput
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
            return withContext(Dispatchers.IO) { groupRepository.save(groupEntity) }
        } else {
            throw NotAuthorizedException(requester, Crud.CREATE to Tables.Group)
        }
    }

    override suspend fun getGroupById(gid: GraphQLUserGroup.ID, requester: MediqToken): MediqGroup {
        if (requester can (Crud.READ to Tables.Group)) {
            return withContext(Dispatchers.IO) { groupRepository.getById(gid.id) }
        } else {
            throw NotAuthorizedException(requester, Crud.READ to Tables.Group)
        }
    }

    override suspend fun getRange(range: GraphQLRangeInput, requester: MediqToken): Collection<MediqGroup> {
        if (requester can (Crud.READ to Tables.Group)) {
            return withContext(Dispatchers.IO) { groupRepository.findAll(range.toPageable()).content }
        } else {
            throw NotAuthorizedException(requester, Crud.READ to Tables.Group)
        }
    }
}