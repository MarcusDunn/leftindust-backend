package com.leftindust.mediq.graphql.types.icd

import com.expediagroup.graphql.annotations.GraphQLName

@GraphQLName("IcdSearchResult")
data class GraphQLIcdSearchResult(
    val destinationEntities: List<GraphQLIcdSimpleEntity>?,
    val error: Boolean,
    val errorMessage: String?,
    val resultChopped: Boolean,
    val wordSuggestionsChopped: Boolean,
    val guessType: GraphQLIcdGuessType,
    val uniqueSearchId: Boolean,
    val words: List<GraphQLIcdGuessWord>?,
)
