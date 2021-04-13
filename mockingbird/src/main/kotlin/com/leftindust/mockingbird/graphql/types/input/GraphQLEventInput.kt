package com.leftindust.mockingbird.graphql.types.input

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.scalars.ID
import com.leftindust.mockingbird.graphql.types.GraphQLTimeInput

data class GraphQLEventInput(
    val title: String,
    val description: String? = null,
    @GraphQLDescription("UTC")
    val start: GraphQLTimeInput,
    @GraphQLDescription("UTC only can be unset / set to null if allDay is true")
    val end: GraphQLTimeInput? = null,
    @GraphQLDescription("defaults to false even if explicitly passed null")
    val allDay: Boolean? = false,
    val doctors: List<ID>? = emptyList(),
    val patients: List<ID>? = emptyList(),
) {
    companion object {
        const val allDayDefault = false
    }

    init {
        if (allDay == true && end != null) {
            throw IllegalArgumentException("you cannot set `end` and `allDay` in GraphQLEventInput. allDay was $allDay and end was $end")
        } else if (allDay != true && end == null) {
            throw IllegalArgumentException("you must set `end` or `allDay` in GraphQLEventInput. allDay was $allDay and end was $end")
        }
    }
}