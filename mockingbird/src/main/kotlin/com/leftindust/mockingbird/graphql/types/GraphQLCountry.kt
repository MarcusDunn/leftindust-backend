package com.leftindust.mockingbird.graphql.types

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.annotations.GraphQLIgnore

enum class GraphQLCountry {
    Canada;

    @GraphQLDescription("gives a list of possible states/provinces/territories that can be used for this country")
    fun associatedStates(): GraphQLProvince {
        return when (this) {
            Canada -> GraphQLCanadianProvince
        }
    }
}
