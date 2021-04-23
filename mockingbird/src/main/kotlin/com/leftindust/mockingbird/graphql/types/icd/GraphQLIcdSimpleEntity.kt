package com.leftindust.mockingbird.graphql.types.icd

import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.expediagroup.graphql.generator.annotations.GraphQLName
import com.leftindust.mockingbird.external.icd.IcdFetcher
import com.leftindust.mockingbird.graphql.types.input.GraphQLReleaseIdInput
import io.ktor.client.call.*
import org.springframework.beans.factory.annotation.Autowired

@GraphQLName("IcdSimpleEntity")
data class GraphQLIcdSimpleEntity(
    private val id: String?,
    private val title: String?,
    val stemId: String?,
    val isLeaf: Boolean,
    val postcoordinationAvailability: GraphQLIcdPostcoordinationAvailability,
    val hasCodingNote: Boolean,
    val hasMaternalChapterLink: Boolean,
    val matchingPVs: List<GraphQLIcdSimplePropertyValue>,
    val propertiesTruncated: Boolean,
    val isResidualOther: Boolean,
    val isResidualUnspecified: Boolean,
    val chapter: String?,
    val theCode: String?,
    val score: Double,
    val titleIsASearchResult: Boolean,
    val titleIsTopScore: Boolean,
    val entityType: GraphQLIcdEntityType,
    val important: Boolean,
    val descendants: List<GraphQLIcdSimpleEntity>,
) {
    suspend fun entity(@Autowired @GraphQLIgnore icdFetcher: IcdFetcher): GraphQLIcdFoundationEntity? {
            return icdFetcher.getDetails(FoundationIcdCode(id ?: return null))
    }

    fun id(asUrl: Boolean? = false): String? {
        val nnAsUrl = asUrl ?: false
        return if (nnAsUrl) {
            id
        } else {
            id?.split("/")?.last()
        }
    }

    fun title(withTags: Boolean? = true): String? {
        val nnWithTags = withTags ?: true
        return if (nnWithTags) {
            title
        } else {
            title?.replace(Regex("<[^>]*>"), "")
        }
    }

    suspend fun linearization(
        @Autowired @GraphQLIgnore icdFetcher: IcdFetcher,
        linearizationName: String? = "mms"
    ): GraphQLIcdMultiVersion? {
        return id?.let { icdFetcher.linearization(linearizationName ?: "mms", FoundationIcdCode(it)) }
    }
}