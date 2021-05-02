package com.leftindust.mockingbird.dao.impl

import com.expediagroup.graphql.generator.execution.OptionalInput
import com.expediagroup.graphql.generator.scalars.ID
import com.leftindust.mockingbird.auth.Authorizer
import com.leftindust.mockingbird.auth.Crud
import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.auth.NotAuthorizedException
import com.leftindust.mockingbird.dao.Tables
import com.leftindust.mockingbird.dao.UserDao
import com.leftindust.mockingbird.dao.entity.MediqUser
import com.leftindust.mockingbird.dao.impl.repository.HibernateDoctorRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernateGroupRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernateUserRepository
import com.leftindust.mockingbird.extensions.toLong
import com.leftindust.mockingbird.graphql.types.input.GraphQLRangeInput
import com.leftindust.mockingbird.graphql.types.input.GraphQLUserEditInput
import com.leftindust.mockingbird.graphql.types.input.GraphQLUserInput
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional
class UserDaoImpl(
    authorizer: Authorizer,
    private val userRepository: HibernateUserRepository,
    private val groupRepository: HibernateGroupRepository,
    private val doctorRepository: HibernateDoctorRepository,
) : UserDao, AbstractHibernateDao(authorizer) {

    override suspend fun findUserByUid(uid: String, requester: MediqToken): MediqUser? {
        return if (requester can (Crud.READ to Tables.User) || (uid == requester.uid && requester.isVerified())) {
            userRepository.findByUniqueId(uid)
        } else {
            throw NotAuthorizedException(requester, Crud.READ to Tables.User)
        }
    }

    override suspend fun getUserByUid(uid: String, requester: MediqToken): MediqUser {
        return if (requester can (Crud.READ to Tables.User) || (uid == requester.uid && requester.isVerified())) {
            userRepository.getByUniqueId(uid)
        } else {
            throw NotAuthorizedException(requester, Crud.READ to Tables.User)
        }
    }

    override suspend fun addUser(
        user: GraphQLUserInput,
        requester: MediqToken
    ): MediqUser {
        return if (requester can (Crud.CREATE to Tables.User)) {
            val group = user.group?.let { groupRepository.getOne(it.toLong()) }
            val mediqUser = MediqUser(user, group)
            userRepository.save(mediqUser)
        } else {
            throw NotAuthorizedException(requester, Crud.CREATE to Tables.User)
        }
    }

    override suspend fun getUsers(
        range: GraphQLRangeInput,
        requester: MediqToken
    ): Collection<MediqUser> {
        return if (requester can (Crud.READ to Tables.User)) {
            userRepository.findAll(range.toPageable()).content
        } else {
            throw NotAuthorizedException(requester, Crud.READ to Tables.Patient)
        }
    }

    override suspend fun updateUser(user: GraphQLUserEditInput, requester: MediqToken): MediqUser {
        return if (requester can (Crud.UPDATE to Tables.User)) {
            userRepository.getByUniqueId(user.uid).apply {
                group = when (user.group) {
                    is OptionalInput.Undefined -> this.group
                    is OptionalInput.Defined -> user.group.value?.let { groupRepository.getOne(it.toLong()) }
                }
            }
        } else {
            throw NotAuthorizedException(requester, Crud.UPDATE to Tables.Patient)
        }
    }

    override suspend fun findByDoctor(did: ID, requester: MediqToken): MediqUser? {
        return if (requester can listOf(Crud.READ to Tables.User, Crud.READ to Tables.Doctor)) {
            doctorRepository.getOne(did.toLong()).user
        } else {
            throw NotAuthorizedException(requester, Crud.READ to Tables.User)
        }
    }
}