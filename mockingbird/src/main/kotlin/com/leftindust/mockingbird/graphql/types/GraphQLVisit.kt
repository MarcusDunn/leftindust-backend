package com.leftindust.mockingbird.graphql.types

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.expediagroup.graphql.generator.annotations.GraphQLName
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.EventDao
import com.leftindust.mockingbird.dao.entity.Visit
import com.leftindust.mockingbird.graphql.types.icd.FoundationIcdCode
import com.leftindust.mockingbird.graphql.types.icd.GraphQLIcdSimpleEntity
import org.springframework.beans.factory.annotation.Autowired
import java.util.*

@GraphQLName("Visit")
data class GraphQLVisit(
    @GraphQLDescription("the id of the visit, will be null if the visit is not persisted to the database")
    val vid: ID,
    val title: String? = null,
    val description: String? = null,
    private val authContext: GraphQLAuthContext,
    private val foundationIcdCode: FoundationIcdCode
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
        foundationIcdCode = visit.icdFoundationCode
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

    fun icd(): GraphQLIcdSimpleEntity {
        TODO()
    }

    // exclude authContext as it should not be relevant for equality (use === for identity)
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GraphQLVisit

        if (vid != other.vid) return false
        if (title != other.title) return false
        if (description != other.description) return false
        if (foundationIcdCode != other.foundationIcdCode) return false

        return true
    }

    override fun hashCode(): Int {
        var result = vid.hashCode()
        result = 31 * result + (title?.hashCode() ?: 0)
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + foundationIcdCode.hashCode()
        return result
    }


}