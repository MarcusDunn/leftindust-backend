package com.leftindust.mockingbird.graphql.types

import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.expediagroup.graphql.generator.annotations.GraphQLName
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.EventDao
import com.leftindust.mockingbird.dao.entity.Visit
import com.leftindust.mockingbird.external.icd.IcdFetcher
import com.leftindust.mockingbird.graphql.types.icd.GraphQLFoundationIcdCode
import com.leftindust.mockingbird.graphql.types.icd.GraphQLIcdFoundationEntity
import org.springframework.beans.factory.annotation.Autowired
import java.util.*

@GraphQLName("Visit")
data class GraphQLVisit(
    val vid: ID,
    val title: String? = null,
    val description: String? = null,
    private val authContext: GraphQLAuthContext,
    private val foundationIcdUrls: List<GraphQLFoundationIcdCode>
) {
    @GraphQLName("VisitId")
    data class ID(val id: UUID)

    // The caller must verify that the Visit entity has been persisted by passing the id explicitly,
    // If you come across a reason to pass a visit back to the frontend that has not been persisted,
    // let me know and we'll have to readjust this constructor.
    constructor(visit: Visit, graphQLAuthContext: GraphQLAuthContext) : this(
        vid = ID(visit.id!!),
        title = visit.title,
        description = visit.description,
        authContext = graphQLAuthContext,
        foundationIcdUrls = visit.icds.map { GraphQLFoundationIcdCode(it) }
    )

    suspend fun event(@GraphQLIgnore @Autowired eventDao: EventDao): GraphQLEvent {
        return eventDao
            .getByVisit(vid, authContext.mediqAuthToken)
            .let { event ->
                GraphQLEvent(
                    event = event,
                    authContext
                )
            }
    }

    suspend fun icds(@GraphQLIgnore @Autowired icdFetcher: IcdFetcher): List<GraphQLIcdFoundationEntity> {
        return foundationIcdUrls.map { icdFetcher.getDetails(it) }
    }
}