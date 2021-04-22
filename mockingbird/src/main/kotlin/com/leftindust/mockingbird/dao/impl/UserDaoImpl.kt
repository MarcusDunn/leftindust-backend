package com.leftindust.mockingbird.dao.impl

import com.expediagroup.graphql.generator.execution.OptionalInput
import com.leftindust.mockingbird.auth.Authorizer
import com.leftindust.mockingbird.auth.Crud
import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.auth.NotAuthorizedException
import com.leftindust.mockingbird.dao.*
import com.leftindust.mockingbird.dao.entity.MediqUser
import com.leftindust.mockingbird.dao.impl.repository.HibernateGroupRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernateUserRepository
import com.leftindust.mockingbird.extensions.*
import com.leftindust.mockingbird.graphql.types.input.GraphQLUserEditInput
import com.leftindust.mockingbird.graphql.types.input.GraphQLUserInput
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional
class UserDaoImpl(
    authorizer: Authorizer,
    private val userRepository: HibernateUserRepository,
    private val groupRepository: HibernateGroupRepository,
) : UserDao, AbstractHibernateDao(authorizer) {

    override suspend fun getUserByUid(uid: String, requester: MediqToken): CustomResult<MediqUser, OrmFailureReason> {
        return if (requester can (Crud.READ to Tables.User) || (uid == requester.uid && requester.isVerified())) {
            val user = userRepository.findByUniqueId(uid)
                ?: return Failure(DoesNotExist("user with uid $uid not found"))
            Success(user)
        } else {
            Failure(NotAuthorized(requester, "not authorized to Read to that specific Patient"))
        }
    }

    override suspend fun addUser(
        user: GraphQLUserInput,
        requester: MediqToken
    ): MediqUser {
        return if (requester can (Crud.CREATE to Tables.User)) {
            val group = user.group_id?.let { groupRepository.getOne(it.toLong()) }
            val mediqUser = MediqUser(user, group)
            userRepository.save(mediqUser)
        } else {
            throw NotAuthorizedException(requester, Crud.CREATE to Tables.User)
        }
    }

    override suspend fun getUsers(
        from: Int,
        to: Int,
        requester: MediqToken
    ): Collection<MediqUser> {
        return if (requester can (Crud.READ to Tables.User)) {
            val size = to - from
            val page = to / size - 1
            userRepository.findAll(PageRequest.of(page, size)).toList()
        } else {
            throw NotAuthorizedException(requester, Crud.READ to Tables.Patient)
        }
    }

    override suspend fun updateUser(user: GraphQLUserEditInput, requester: MediqToken): MediqUser {
        return if (requester can (Crud.UPDATE to Tables.User)) {
            userRepository.getUserByUniqueId(user.uid).apply {
                group = when (user.group_id) {
                    is OptionalInput.Undefined -> this.group
                    is OptionalInput.Defined -> user.group_id.value?.let { groupRepository.getOne(it.toLong()) }
                }
            }
        } else {
            throw NotAuthorizedException(requester, Crud.UPDATE to Tables.Patient)
        }
    }
}