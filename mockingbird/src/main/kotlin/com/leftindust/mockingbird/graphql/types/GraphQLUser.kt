package com.leftindust.mockingbird.graphql.types

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.expediagroup.graphql.generator.annotations.GraphQLName
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.AuthorizationDao
import com.leftindust.mockingbird.dao.DoctorDao
import com.leftindust.mockingbird.dao.NameInfoDao
import com.leftindust.mockingbird.dao.UserDao
import com.leftindust.mockingbird.dao.entity.Action
import com.leftindust.mockingbird.dao.entity.MediqGroup
import com.leftindust.mockingbird.dao.entity.MediqUser
import com.leftindust.mockingbird.dao.patient.ReadPatientDao
import com.leftindust.mockingbird.external.firebase.UserFetcher
import com.leftindust.mockingbird.graphql.types.input.GraphQLPermissionInput
import org.springframework.beans.factory.annotation.Autowired
import java.util.*

@GraphQLName("User")
data class GraphQLUser(
    val uid: String,
    val group: Group? = null,
    private val authContext: GraphQLAuthContext
) {
    constructor(mediqUser: MediqUser, graphQLAuthContext: GraphQLAuthContext) : this(
        uid = mediqUser.uniqueId,
        group = mediqUser.group?.let { Group(it) },
        authContext = graphQLAuthContext,
    )

    @GraphQLDescription(
        """
        The name of the user.
    """
    )
    suspend fun name(@GraphQLIgnore @Autowired nameInfoDao: NameInfoDao): GraphQLNameInfo? {
        return nameInfoDao.findByUniqueId(uid, authContext.mediqAuthToken)?.let { GraphQLNameInfo(it) }
    }

    @GraphQLDescription(
        """
        Weather the user is regestered in mockingbird.
    """
    )
    suspend fun isRegistered(@GraphQLIgnore @Autowired userDao: UserDao): Boolean {
        return userDao.findUserByUid(uid, authContext.mediqAuthToken) != null
    }

    @GraphQLDescription(
        """
        The firebase-specific info for this user.
    """
    )
    suspend fun firebaseUserInfo(@GraphQLIgnore @Autowired userFetcher: UserFetcher): GraphQLFirebaseInfo =
        GraphQLFirebaseInfo(userFetcher.getUserInfo(uid, authContext.mediqAuthToken))

    @GraphQLDescription(
        """
        The permissions this user possesses
    """
    )
    suspend fun permissions(@GraphQLIgnore @Autowired authorizationDao: AuthorizationDao): GraphQLPermissions {
        return GraphQLPermissions(authorizationDao.getRolesForUserByUid(uid))
    }

    @GraphQLDescription(
        """
        The corresponding doctor for this user if it exists.
    """
    )
    suspend fun doctor(@GraphQLIgnore @Autowired doctorDao: DoctorDao): GraphQLDoctor? {
        return doctorDao.getByUser(uid, authContext.mediqAuthToken)?.let { GraphQLDoctor(it, authContext) }
    }

    @GraphQLDescription(
        """
        The corresponding patient for this user if it exists.
    """
    )
    suspend fun patient(@GraphQLIgnore @Autowired patientDao: ReadPatientDao): GraphQLPatient? {
        return patientDao.getByUser(uid, authContext.mediqAuthToken)?.let { GraphQLPatient(it, authContext) }
    }

    @GraphQLDescription(
        """
        Weather the user has permission do to a given action.
    """
    )
    suspend fun hasPermission(
        @GraphQLIgnore @Autowired authorizationDao: AuthorizationDao,
        perm: GraphQLPermissionInput
    ): Boolean = authorizationDao.getRolesForUserByUid(uid).any { it.action.isSuperset(Action(perm)) }


    data class Group(
        val gid: ID,
        val name: String
    ) {
        @GraphQLName("GroupId")
        data class ID(val id: UUID)

        constructor(group: MediqGroup) : this(
            gid = ID(group.id!!),
            name = group.name
        )
    }
}


