package com.leftindust.mediq.graphql.types.icd

import com.expediagroup.graphql.annotations.GraphQLName

@GraphQLName("IcdLinearizationEntity")
data class GraphQLIcdLinearizationEntity(
    val context: String?,
    val id: String?,
    val title: GraphQLIcdLanguageSpecificText,
    val definition: GraphQLIcdLanguageSpecificText,
    val longDefinition: GraphQLIcdLanguageSpecificText,
    val fullySpecifiedName: GraphQLIcdLanguageSpecificText,
    val source: String?,
    val code: String?,
    val codingNote: GraphQLIcdLanguageSpecificText,
    val blockId: String?,
    val codeRange: String?,
    val classKind: String?,
    val child: List<String>?,
    val parent: List<String>?,
    val foundationChildElsewhere: List<GraphQLIcdTerm>?,
    val indexTerm: List<GraphQLIcdTerm>?,
    val inclusion: List<GraphQLIcdTerm>?,
    val exclusion: List<GraphQLIcdTerm>?,
    val postcoordinationScale: GraphQLIcdPostCoordinationScaleInfo?,
    val browserUrl: String?,
)