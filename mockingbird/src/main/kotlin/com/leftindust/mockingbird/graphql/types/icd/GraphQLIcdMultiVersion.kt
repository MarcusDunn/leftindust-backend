package com.leftindust.mockingbird.graphql.types.icd

import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.expediagroup.graphql.generator.annotations.GraphQLName
import com.leftindust.mockingbird.external.icd.IcdFetcher
import com.leftindust.mockingbird.external.icd.impl.IcdMultiVersion
import com.leftindust.mockingbird.graphql.types.input.GraphQLReleaseIdInput
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

    constructor(icdMultiVersion: IcdMultiVersion) : this(
        context = icdMultiVersion.`@context`,
        id = icdMultiVersion.`@id`,
        title = GraphQLIcdLanguageSpecificText(icdMultiVersion.title),
        latestRelease = icdMultiVersion.latestRelease,
        release = icdMultiVersion.release,
    )
}