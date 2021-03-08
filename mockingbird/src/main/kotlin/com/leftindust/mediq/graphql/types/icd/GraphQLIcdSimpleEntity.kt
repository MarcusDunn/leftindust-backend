package com.leftindust.mediq.graphql.types.icd

import com.expediagroup.graphql.annotations.GraphQLIgnore
import com.expediagroup.graphql.annotations.GraphQLName
import com.leftindust.mediq.extensions.Failure
import com.leftindust.mediq.extensions.Success
import com.leftindust.mediq.external.icd.IcdFetcher
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
        return when (val result = icdFetcher.getDetails(FoundationIcdCode(id ?: return null))) {
            is Failure -> null
            is Success -> result.value
        }
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
        return if (id == null) null else {
            when (val result = icdFetcher.linearization(linearizationName ?: "mms", FoundationIcdCode(id))) {
                is Failure -> null
                is Success -> result.value
            }
        }
    }

    suspend fun linearizationEntity(
        @Autowired @GraphQLIgnore icdFetcher: IcdFetcher,
        releaseId: String? = "2019-04"
    ): GraphQLIcdLinearizationEntity? {
        return if (id == null) null else {
            when (val result = icdFetcher.getLinearizationEntity(releaseId ?: "2019-04", FoundationIcdCode(id))) {
                is Failure -> null
                is Success -> result.value
            }
        }
    }
}