package com.leftindust.mediq.dao.impl

import com.google.gson.JsonObject
import com.leftindust.mediq.auth.Authorizer
import com.leftindust.mediq.auth.Crud
import com.leftindust.mediq.auth.MediqToken
import com.leftindust.mediq.dao.*
import com.leftindust.mediq.dao.entity.Action
import com.leftindust.mediq.dao.entity.MediqUser
import com.leftindust.mediq.dao.entity.UserSettings
import com.leftindust.mediq.dao.impl.repository.HibernateGroupRepository
import com.leftindust.mediq.dao.impl.repository.HibernateUserRepository
import com.leftindust.mediq.extensions.CustomResult
import com.leftindust.mediq.extensions.Failure
import com.leftindust.mediq.extensions.Success
import com.leftindust.mediq.extensions.toLong
import com.leftindust.mediq.graphql.types.input.GraphQLUserInput
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
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
    val logger: Logger = LogManager.getLogger()


    override suspend fun getUserByUid(uid: String, requester: MediqToken): CustomResult<MediqUser, OrmFailureReason> {
        val user = userRepository.getUserByUniqueId(uid)

        val isPermitted = run {
            val requiredPermission = Action(
                referencedTableName = Tables.User,
                permissionType = Crud.READ,
                rowId = user?.id
            )
            requester can requiredPermission
        }

        return if (isPermitted) {
            user ?: return Failure(DoesNotExist())
            Success(user)
        } else {
            Failure(NotAuthorized(requester,  "not authorized to Read to that specific Patient"))
        }
    }

    override suspend fun setUserSettingsByUid(
        uid: String,
        version: Int,
        settings: JsonObject,
        requester: MediqToken
    ): CustomResult<MediqUser, OrmFailureReason> {

        /* don't null check until we know we have permission to read */
        val user = userRepository.getUserByUniqueId(uid)

        val isPermitted = run {
            val updateThisRow = Action(
                referencedTableName = Tables.User,
                permissionType = Crud.UPDATE,
                rowId = user?.id,
            )
            requester can updateThisRow
        }

        return if (isPermitted) {
            val newSettings = UserSettings(
                version = version,
                settingsJSON = settings.toString()
            )
            user?.settings ?: return Failure(DoesNotExist())
            user.settings = newSettings
            Success(user)
        } else {
            Failure(NotAuthorized(requester))
        }
    }

    override suspend fun addUser(user: MediqUser, requester: MediqToken): CustomResult<MediqUser, OrmFailureReason> {
        return if (requester can (Crud.CREATE to Tables.User)) {
            if (userRepository.getUserByUniqueId(user.uniqueId) == null) {
                Success(userRepository.save(user))
            } else {
                return Failure(AlreadyExists())
            }
        } else {
            Failure(NotAuthorized(requester, "cannot ${Crud.CREATE to Tables.User}"))
        }
    }

    override suspend fun addUser(user: GraphQLUserInput, requester: MediqToken): CustomResult<MediqUser, OrmFailureReason> {
        val mediqUser = MediqUser(
            uniqueId = user.uid,
            group = user.group_id?.let { groupRepository.getByGid(it.toLong()) },
            settings = UserSettings(
                version = user.settings_version,
                settingsJSON = user.settings.json
            )
        )
        return addUser(mediqUser, requester)
    }

    override suspend fun getUsers(from: Int, to: Int, requester: MediqToken): CustomResult<List<MediqUser>, OrmFailureReason> {
        val size = to - from
        val page = to / size - 1
        return authenticateAndThen(requester, Crud.READ to Tables.User) {
            userRepository.findAll(PageRequest.of(page, size)).toList()
        }
    }
}