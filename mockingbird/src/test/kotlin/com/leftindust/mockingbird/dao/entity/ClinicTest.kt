package com.leftindust.mockingbird.dao.entity

import com.leftindust.mockingbird.extensions.gqlID
import com.leftindust.mockingbird.graphql.types.GraphQLAddressType
import com.leftindust.mockingbird.graphql.types.GraphQLCanadianProvince
import com.leftindust.mockingbird.graphql.types.GraphQLCountry
import com.leftindust.mockingbird.graphql.types.input.GraphQLAddressEditInput
import com.leftindust.mockingbird.graphql.types.input.GraphQLClinicEditInput
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class ClinicTest {

    @Test
    fun setByGqlInput() {
        val clinic = Clinic(
            name = "sir Aurthur Curry's hospice for terminal deaf and blind children",
            address = Address(
                type = GraphQLAddressType.Apartment,
                address = "442 2nd W",
                city = "tambie",
                countryState = CountryState(
                    GraphQLCountry.Canada,
                    GraphQLCanadianProvince.Provinces.NewBrunswick.name
                ),
                postalCode = "fe3232",
            ),
            doctors = setOf(mockk())
        )

        clinic.setByGqlInput(
            GraphQLClinicEditInput(
                id = gqlID(10),
                address = GraphQLAddressEditInput(
                    address = "main st",
                    addressType = GraphQLAddressType.Home
                )
            ), mockk()
        )

        assertEquals(
            Address(
                type = GraphQLAddressType.Home,
                address = "main st",
                city = "tambie",
                countryState = CountryState(
                    GraphQLCountry.Canada,
                    GraphQLCanadianProvince.Provinces.NewBrunswick.name
                ),
                postalCode = "fe3232",
            ),
            clinic.address,
        )
    }
}