package com.leftindust.mediq.graphql.types

import com.expediagroup.graphql.annotations.GraphQLDescription
import com.expediagroup.graphql.annotations.GraphQLIgnore
import com.expediagroup.graphql.annotations.GraphQLName
import com.expediagroup.graphql.scalars.ID
import com.leftindust.mediq.auth.GraphQLAuthContext
import com.leftindust.mediq.dao.DoctorDao
import com.leftindust.mediq.dao.PatientDao
import com.leftindust.mediq.dao.entity.Visit
import com.leftindust.mediq.extensions.gqlID
import com.leftindust.mediq.extensions.toLong
import com.leftindust.mediq.graphql.types.icd.FoundationIcdCode
import com.leftindust.mediq.graphql.types.icd.GraphQLIcdSimpleEntity
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
        return doctorDao
            .getByVisit(vid?.toLong(), authToken)
            .getOrThrow()
            .let { GraphQLDoctor(it, it.id!!, authContext) } // safe nn assert as we just got from DB
    }

    suspend fun patient(@GraphQLIgnore @Autowired patientDao: PatientDao): GraphQLPatient = patientDao
        .getByVisit(vid?.toLong(), authToken)
        .getOrThrow()
        .let { GraphQLPatient(it, it.id!!, authContext) }

    fun icd(): GraphQLIcdSimpleEntity {
        TODO()
    }
}