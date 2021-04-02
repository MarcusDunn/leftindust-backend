package com.leftindust.mockingbird.graphql.queries

import com.leftindust.mockingbird.graphql.types.GraphQLCountry
import org.springframework.stereotype.Component

@Component
class CountryQuery {
    fun country(country: GraphQLCountry): List<String> = country.associatedStates()
}