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
import com.leftindust.mockingbird.dao.entity.MediqUser
import com.leftindust.mockingbird.dao.patient.ReadPatientDao
import com.leftindust.mockingbird.external.firebase.UserFetcher
import com.leftindust.mockingbird.graphql.types.input.GraphQLPermissionInput
import org.springframework.beans.factory.annotation.Autowired

@GraphQLName("User")
data class GraphQLUser(
    val uid: String,
    val group: GraphQLUserGroup? = null, // TODO: 2022-03-02 make sure this does not cause a lazy initialization exception when accessed as a feild
    private val authContext: GraphQLAuthContext
) {
    constructor(mediqUser: MediqUser, graphQLAuthContext: GraphQLAuthContext) : this(
        uid = mediqUser.uniqueId,
        group = mediqUser.group?.let { GraphQLUserGroup(it) },
        authContext = graphQLAuthContext,
    )

    @GraphQLDescription(
        """
        The name of the user.
    """
    )
    suspend fun name(
        @GraphQLIgnore @Autowired nameInfoDao: NameInfoDao
    ): GraphQLNameInfo? = nameInfoDao.findByUniqueId(uid, authContext.mediqAuthToken)?.let { GraphQLNameInfo(it) }


    @GraphQLDescription(
        """
        Weather the user is registered in mockingbird.
    """
    )
    suspend fun isRegistered(
        @GraphQLIgnore @Autowired userDao: UserDao
    ): Boolean = userDao.findUserByUid(uid, authContext.mediqAuthToken) != null


    @GraphQLDescription(
        """
        The firebase-specific info for this user.
    """
    )
    suspend fun firebaseUserInfo(
        @GraphQLIgnore @Autowired userFetcher: UserFetcher
    ): GraphQLFirebaseInfo = GraphQLFirebaseInfo(userFetcher.getUserInfo(uid, authContext.mediqAuthToken))

    @GraphQLDescription(
        """
        The permissions this user possesses
    """
    )
    suspend fun permissions(
        @GraphQLIgnore @Autowired authorizationDao: AuthorizationDao
    ): GraphQLPermissions = GraphQLPermissions(authorizationDao.getRolesForUserByUid(uid))


    @GraphQLDescription(
        """
        The corresponding doctor for this user if it exists.
    """
    )
    suspend fun doctor(
        @GraphQLIgnore @Autowired doctorDao: DoctorDao
    ): GraphQLDoctor? = doctorDao.getByUser(uid, authContext.mediqAuthToken)?.let { GraphQLDoctor(it, authContext) }


    @GraphQLDescription(
        """
        The corresponding patient for this user if it exists.
    """
    )
    suspend fun patient(
        @GraphQLIgnore @Autowired patientDao: ReadPatientDao
    ): GraphQLPatient? = patientDao.getByUser(uid, authContext.mediqAuthToken)?.let { GraphQLPatient(it, authContext) }


    @GraphQLDescription(
        """
        Weather the user has permission do to a given action.
    """
    )
    suspend fun hasPermission(
        @GraphQLIgnore @Autowired authorizationDao: AuthorizationDao,
        perm: GraphQLPermissionInput
    ): Boolean = authorizationDao.getRolesForUserByUid(uid).any { it.action.isSuperset(Action(perm)) }
}


