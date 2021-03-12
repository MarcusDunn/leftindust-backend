package com.leftindust.mockingbird.graphql.types.icd

import com.expediagroup.graphql.annotations.GraphQLIgnore
import com.expediagroup.graphql.annotations.GraphQLName
import com.leftindust.mockingbird.extensions.Failure
import com.leftindust.mockingbird.extensions.Success
import com.leftindust.mockingbird.external.icd.IcdFetcher
import com.leftindust.mockingbird.external.icd.impl.IcdMultiVersion
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.beans.factory.annotation.Autowired

@GraphQLName("IcdMultiVersion")
data class GraphQLIcdMultiVersion(
    val context: String?,
    val id: String?,
    val title: GraphQLIcdLanguageSpecificText,
    private val latestRelease: String?,
    private val release: List<String>
) {
    suspend fun latestEntity(
        @Autowired @GraphQLIgnore icdFetcher: IcdFetcher,
    ): GraphQLIcdLinearizationEntity? {
        return if (id == null || latestRelease == null) null else {
            val releaseId = releaseIdFromUri(latestRelease)
            when (val result = icdFetcher.getLinearizationEntity(releaseId, FoundationIcdCode(id))) {
                is Failure -> null
                is Success -> result.value
            }
        }
    }

    private fun releaseIdFromUri(latestRelease: String): String {
        return latestRelease.split("/").reversed()[2]
    }

    suspend fun entities(@Autowired @GraphQLIgnore icdFetcher: IcdFetcher): List<GraphQLIcdLinearizationEntity?> {
        val returnList = emptyList<GraphQLIcdLinearizationEntity?>().toMutableList()
        release.asFlow().map {
            when (val result =
                icdFetcher.getLinearizationEntity(releaseIdFromUri(it), FoundationIcdCode(id ?: return@map null))) {
                is Failure -> null
                is Success -> result.value
            }
        }.toList(returnList)
        return returnList.filterNotNull()
    }

    constructor(icdMultiVersion: IcdMultiVersion) : this(
        context = icdMultiVersion.`@context`,
        id = icdMultiVersion.`@id`,
        title = GraphQLIcdLanguageSpecificText(icdMultiVersion.title),
        latestRelease = icdMultiVersion.latestRelease,
        release = icdMultiVersion.release,
    )
}