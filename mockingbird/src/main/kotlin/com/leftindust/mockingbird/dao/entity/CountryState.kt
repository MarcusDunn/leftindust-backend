package com.leftindust.mockingbird.dao.entity

import com.leftindust.mockingbird.graphql.types.GraphQLCountry
import javax.persistence.Embeddable
import javax.persistence.EnumType
import javax.persistence.Enumerated

@Embeddable
class CountryState(
    @Enumerated(EnumType.STRING)
    var country: GraphQLCountry,
    province: String,
) {
    init {
        if (isValidStateForCountry(province)) {
            // ok
        } else {
            throw IllegalArgumentException("the province must be contained within the country, possible values are ${country.associatedStates()}")
        }
    }

    var province = province
        set(value) {
            if (isValidStateForCountry(value)) {
                field = value
            } else {
                throw IllegalArgumentException("the province must be contained within the country, possible values are ${country.associatedStates()}")
            }
        }

    private fun isValidStateForCountry(value: String) = country.associatedStates().contains(value)
}

