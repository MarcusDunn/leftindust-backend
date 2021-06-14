package com.leftindust.mockingbird.graphql.types.icd

import com.expediagroup.graphql.generator.annotations.GraphQLName
import com.leftindust.mockingbird.external.icd.impl.IcdLinearizationEntity

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
) {
    constructor(icdLinearizationEntity: IcdLinearizationEntity) : this(
        context = icdLinearizationEntity.`@context`,
                id = icdLinearizationEntity.`@id`,
                title = GraphQLIcdLanguageSpecificText(icdLinearizationEntity.title),
                definition = GraphQLIcdLanguageSpecificText(icdLinearizationEntity.definition),
                longDefinition = GraphQLIcdLanguageSpecificText(icdLinearizationEntity.longDefinition),
                fullySpecifiedName = GraphQLIcdLanguageSpecificText(icdLinearizationEntity.fullySpecifiedName),
                source = icdLinearizationEntity.source,
                code = icdLinearizationEntity.code,
                codingNote = GraphQLIcdLanguageSpecificText(icdLinearizationEntity.codingNote),
                blockId = icdLinearizationEntity.blockId,
                codeRange = icdLinearizationEntity.codeRange,
                classKind = icdLinearizationEntity.classKind,
                child = icdLinearizationEntity.child,
                parent = icdLinearizationEntity.parent,
                foundationChildElsewhere = icdLinearizationEntity.foundationChildElsewhere,
                indexTerm = icdLinearizationEntity.indexTerm,
                inclusion = icdLinearizationEntity.inclusion,
                exclusion = icdLinearizationEntity.exclusion,
                postcoordinationScale = icdLinearizationEntity.postcoordinationScale,
                browserUrl = icdLinearizationEntity.browserUrl,
    )
}