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
        isValidStateForCountryOrThrow(province)
    }

    var province = province
        set(value) = isValidStateForCountryOrThrow(value)


    private fun isValidStateForCountryOrThrow(value: String) = with(country.associatedStates()) {
        if (!this.asShortStrings().contains(value) && !this.asStrings().contains(value)) {
            throw IllegalArgumentException(
                """
                        |the province must be contained within the country, possible values are
                        | ${country.associatedStates().asStrings()} or ${country.associatedStates().asShortStrings()}
                        | """.trimMargin()
            )
        }
    }
}