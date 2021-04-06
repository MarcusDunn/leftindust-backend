package com.leftindust.mockingbird.dao.impl

import com.google.gson.JsonObject
import com.leftindust.mockingbird.auth.Authorizer
import com.leftindust.mockingbird.auth.Crud
import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.dao.*
import com.leftindust.mockingbird.dao.entity.Action
import com.leftindust.mockingbird.dao.entity.MediqUser
import com.leftindust.mockingbird.dao.impl.repository.HibernateGroupRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernateUserRepository
import com.leftindust.mockingbird.extensions.*
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
            val user = userRepository.getUserByUniqueId(uid)
                ?: return Failure(DoesNotExist("user with uid $uid not found"))
            Success(user)
        } else {
            Failure(NotAuthorized(requester, "not authorized to Read to that specific Patient"))
        }
    }
    override suspend fun addUser(
        user: GraphQLUserInput,
        requester: MediqToken
    ): CustomResult<MediqUser, OrmFailureReason> {
        return if (requester can (Crud.CREATE to Tables.User)) {
            val group = user.group_id?.let {
                groupRepository.getOneOrNull(it.toLong())
                    ?: return Failure(DoesNotExist("that group does not exist"))
            } // if no group is passed, no problem, if group is passed and does not exist, throw an error
            val mediqUser = MediqUser(user, group)
            if (userRepository.getUserByUniqueId(user.uid) == null) {
                Success(userRepository.save(mediqUser))
            } else {
                Failure(AlreadyExists())
            }
        } else {
            Failure(NotAuthorized(requester, "cannot ${Crud.CREATE to Tables.User}"))
        }
    }

    override suspend fun getUsers(
        from: Int,
        to: Int,
        requester: MediqToken
    ): CustomResult<List<MediqUser>, OrmFailureReason> {
        val size = to - from
        val page = to / size - 1
        return authenticateAndThen(requester, Crud.READ to Tables.User) {
            userRepository.findAll(PageRequest.of(page, size)).toList()
        }
    }
}