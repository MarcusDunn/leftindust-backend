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

    var province = province
        set(value) {
            field = country.provinceShortToLong(value)
        }
}