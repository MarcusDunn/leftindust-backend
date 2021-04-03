package com.leftindust.mockingbird.graphql.types

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.expediagroup.graphql.generator.annotations.GraphQLName
import com.expediagroup.graphql.generator.scalars.ID
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.VisitDao
import com.leftindust.mockingbird.dao.entity.Visit
import com.leftindust.mockingbird.extensions.gqlID
import com.leftindust.mockingbird.extensions.toLong
import com.leftindust.mockingbird.graphql.types.icd.FoundationIcdCode
import com.leftindust.mockingbird.graphql.types.icd.GraphQLIcdSimpleEntity
import org.springframework.beans.factory.annotation.Autowired

@GraphQLName("Visit")
data class GraphQLVisit(
    @GraphQLDescription("the id of the visit, will be null if the visit is not persisted to the database")
    val vid: ID?,
    val title: String? = null,
    val description: String? = null,
    private val authContext: GraphQLAuthContext,
    private val foundationIcdCode: FoundationIcdCode
) {
    private val authToken = authContext.mediqAuthToken

    // The caller must verify that the Visit entity has been persisted by passing the id explicitly,
    // If you come across a reason to pass a visit back to the frontend that has not been persisted,
    // let me know and we'll have to readjust this constructor.
    constructor(visit: Visit, id: Long, graphQLAuthContext: GraphQLAuthContext) : this(
        vid = gqlID(id),
        title = visit.title,
        description = visit.description,
        authContext = graphQLAuthContext,
        foundationIcdCode = visit.icdFoundationCode
    ) {
        // runtime check that someone isn't sending inconsistent data
        if (visit.id != id) throw RuntimeException("inconsistency between visit.id and id (visit.id: ${visit.id} id: $id)")
    }

    suspend fun event(@GraphQLIgnore @Autowired visitDao: VisitDao): GraphQLEvent {
        val nnVid = vid?.toLong() ?: throw IllegalArgumentException("cannot get doctor on visit with null vid")
        return visitDao.getVisitByVid(nnVid, authToken)
            .getOrThrow()
            .event
            .let { GraphQLEvent(it, it.id!!.toLong()) }
    }

    suspend fun doctor(@GraphQLIgnore @Autowired visitDao: VisitDao): GraphQLDoctor {
        val nnVid = vid?.toLong() ?: throw IllegalArgumentException("cannot get doctor on visit with null vid")
        return visitDao
            .getVisitByVid(nnVid, authToken)
            .getOrThrow()
            .doctor
            .let { GraphQLDoctor(it, it.id!!, authContext) }
    }

    suspend fun patient(@GraphQLIgnore @Autowired visitDao: VisitDao): GraphQLPatient {
        val nnVid = vid?.toLong() ?: throw IllegalArgumentException("cannot get patient on visit with null vid")
        return visitDao
            .getVisitByVid(nnVid, authToken)
            .getOrThrow()
            .patient
            .let { GraphQLPatient(it, it.id!!, authContext) }
    }

    fun icd(): GraphQLIcdSimpleEntity {
        TODO()
    }
}