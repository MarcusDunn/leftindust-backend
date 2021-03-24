package com.leftindust.mockingbird.graphql.types

import com.expediagroup.graphql.annotations.GraphQLDescription
import com.expediagroup.graphql.annotations.GraphQLIgnore
import com.expediagroup.graphql.annotations.GraphQLName
import com.expediagroup.graphql.scalars.ID
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.DoctorDao
import com.leftindust.mockingbird.dao.PatientDao
import com.leftindust.mockingbird.dao.entity.Visit
import com.leftindust.mockingbird.extensions.gqlID
import com.leftindust.mockingbird.extensions.toLong
import com.leftindust.mockingbird.graphql.types.icd.FoundationIcdCode
import com.leftindust.mockingbird.graphql.types.icd.GraphQLIcdSimpleEntity
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

@GraphQLName("Visit")
data class GraphQLVisit(
    @GraphQLDescription("the id of the visit, will be null if the visit is not persisted to the database")
    val vid: ID?,
    val timeBooked: GraphQLTime,
    val timeOfVisit: GraphQLTime,
    val title: String? = null,
    val description: String? = null,
    private val authContext: GraphQLAuthContext,
    private val foundationIcdCode: FoundationIcdCode
) {
    private val logger: Logger = LogManager.getLogger()


    // The caller must verify that the Visit entity has been persisted by passing the id explicitly,
    // If you come across a reason to pass a visit back to the frontend that has not been persisted,
    // let me know and we'll have to readjust this constructor.
    constructor(visit: Visit, id: Long, graphQLAuthContext: GraphQLAuthContext) : this(
        vid = gqlID(id),
        timeBooked = GraphQLTime(visit.timeBooked),
        timeOfVisit = GraphQLTime(visit.timeOfVisit),
        title = visit.title,
        description = visit.description,
        authContext = graphQLAuthContext,
        foundationIcdCode = visit.icdFoundationCode
    ) {
        // runtime check that someone isn't sending inconsistent data
        if (visit.id != id) throw RuntimeException("inconsistency between visit.id and id")
    }

    private val authToken = authContext.mediqAuthToken
    suspend fun doctor(@GraphQLIgnore @Autowired doctorDao: DoctorDao): GraphQLDoctor {
        val nnVid = vid?.toLong() ?: throw IllegalArgumentException("cannot get doctor on visit with null vid")
        return doctorDao
            .getByVisit(nnVid, authToken)
            .getOrThrow()
            .let { GraphQLDoctor(it, it.id!!, authContext) }
    }

    suspend fun patient(@GraphQLIgnore @Autowired patientDao: PatientDao): GraphQLPatient {
        val nnVid = vid?.toLong() ?: throw IllegalArgumentException("cannot get patient on visit with null vid")
        return patientDao
            .getByVisit(nnVid, authToken)
            .getOrThrow()
            .let { GraphQLPatient(it, it.id!!, authContext) }
    }

    fun icd(): GraphQLIcdSimpleEntity {
        TODO()
    }
}