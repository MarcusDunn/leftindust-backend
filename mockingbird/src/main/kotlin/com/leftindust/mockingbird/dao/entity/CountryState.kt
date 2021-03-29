package com.leftindust.mockingbird.dao.entity

import com.leftindust.mockingbird.graphql.types.GraphQLCanadianProvince
import com.leftindust.mockingbird.graphql.types.GraphQLCountry
import javax.persistence.Embeddable
import javax.persistence.EnumType
import javax.persistence.Enumerated

@Embeddable
class CountryState(
    @Enumerated(EnumType.STRING)
    val country: GraphQLCountry,
    @Enumerated(EnumType.STRING)
    val province: GraphQLCanadianProvince,
)

