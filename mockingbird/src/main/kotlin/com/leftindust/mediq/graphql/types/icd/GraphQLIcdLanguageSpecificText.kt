package com.leftindust.mediq.graphql.types.icd

import com.expediagroup.graphql.annotations.GraphQLName
import com.leftindust.mediq.external.icd.impl.IcdLanguageSpecificText

@GraphQLName("IcdLanguageSpecificText")
data class GraphQLIcdLanguageSpecificText(
    val language: String?,
    val value: String?,
) {
    constructor(languageSpecificText: IcdLanguageSpecificText) : this(
        language = languageSpecificText.`@language`,
        value = languageSpecificText.`@value`,
    )
}
