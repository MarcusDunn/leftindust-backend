package integration.util

import com.leftindust.mockingbird.dao.entity.*
import com.leftindust.mockingbird.dao.entity.enums.Ethnicity
import com.leftindust.mockingbird.dao.entity.enums.Relationship
import com.leftindust.mockingbird.dao.entity.enums.Sex
import com.leftindust.mockingbird.extensions.gqlID
import com.leftindust.mockingbird.graphql.types.*
import com.leftindust.mockingbird.graphql.types.input.*
import java.sql.Date
import java.sql.Timestamp
import java.time.LocalDate

object EntityStore {
    fun patient(testName: String) = Patient(
        nameInfo = NameInfo(
            firstName = "marcus",
            middleName = testName,
            lastName = "dunn",
        ),
        dateOfBirth = Date.valueOf(LocalDate.of(2020, 1, 2)),
        addresses = setOf(
            Address(
                type = GraphQLAddressType.Home,
                city = "North Vancouver",
                countryState = CountryState(
                    country = GraphQLCountry.Canada,
                    province = GraphQLCanadianProvince.Provinces.Alberta.name
                ),
                address = "874 West 1st Street",
                postalCode = "y7h1p4",
            )
        ),
        emails = setOf(Email(email = "hello@world.ca", type = GraphQLEmailType.Personal)),
        phones = setOf(Phone("6632231111", GraphQLPhoneType.Home)),
        sex = Sex.Male,
        gender = Sex.Male.name,
        ethnicity = Ethnicity.White,
        insuranceNumber = "marcus",
        contacts = emptySet(),
        doctors = emptySet(),
    )

    fun doctor(testName: String) = Doctor(
        nameInfo = NameInfo(
            firstName = "dan",
            lastName = testName,
            middleName = "the man",
        ),
        dateOfBirth = Date.valueOf(LocalDate.of(2018, 1, 24)),
        addresses = setOf(
            Address(
                type = GraphQLAddressType.Home,
                address = "999 East 7th Drive",
                city = "West Vancouver",
                countryState = CountryState(
                    country = GraphQLCountry.Canada,
                    province = GraphQLCanadianProvince.Provinces.Alberta.name
                ),
                postalCode = "y7h1p5",
            )
        ),
        emails = setOf(Email(email = "world@hello.ca", type = GraphQLEmailType.Personal)),
        phones = setOf(Phone("6632231211", GraphQLPhoneType.Home)),
        title = "sir",
        patients = emptySet(),
        schedule = Schedule(),
    )

    fun graphQLPatientInput(testName: String) = GraphQLPatientInput(
        nameInfo = GraphQLNameInfoInput(
            firstName = "aydan",
            middleName = testName,
            lastName = "gaite",
        ),
        phones = listOf(
            GraphQLPhoneInput(
                number = "11111111",
                type = GraphQLPhoneType.Work,
            )
        ),
        dateOfBirth = GraphQLDateInput(
            day = 12,
            month = GraphQLMonth.Apr,
            year = 1948
        ),
        addresses = listOf(
            GraphQLAddressInput(
                addressType = GraphQLAddressType.Home,
                address = "6732 main st",
                city = "East Vancouver",
                country = GraphQLCountry.Canada,
                province = GraphQLCanadianProvince.Provinces.NewBrunswick.name,
                postalCode = "h221234",
            )
        ),
        emails = listOf(
            GraphQLEmailInput(
                type = GraphQLEmailType.School,
                email = "hello@mars.ca",
            )
        ),
        insuranceNumber = gqlID(111111111),
        sex = Sex.Male,
        ethnicity = Ethnicity.AmericanAboriginal,
        emergencyContacts = listOf(
            GraphQLEmergencyContactInput(
                firstName = "mom firstName",
                middleName = "mom middleName",
                lastName = "mom lastName",
                relationship = Relationship.Parent,
                phones = listOf(
                    GraphQLPhoneInput(
                        number = "111111111",
                        type = GraphQLPhoneType.Work,
                    ),
                    GraphQLPhoneInput(
                        number = "223223222",
                        type = GraphQLPhoneType.Home,
                    ),
                ),
                emails = listOf(
                    GraphQLEmailInput(
                        type = GraphQLEmailType.School,
                        email = "bye@saturn.uk",
                    )
                )
            )
        ),
    )

    fun graphQLEventInput(testName: String) =
        GraphQLEventInput(
            title = testName,
            description = "some description",
            start = GraphQLTimeInput(Timestamp.valueOf("2020-01-02 09:00:00")),
            end = GraphQLTimeInput(Timestamp.valueOf("2020-01-02 10:00:00")),
            doctors = listOf(),
            patients = listOf(),
        )

    fun event(testName: String) = Event(
        title = testName,
        description = "some other description",
        startTime = Timestamp.valueOf("2020-01-02 11:00:00"),
        endTime = Timestamp.valueOf("2020-01-02 12:00:00"),
        doctors = emptySet(),
        patients = emptySet()
    )

    fun address(testName: String) = Address(
            type = GraphQLAddressType.Other,
            address = testName,
            city = "East Vancouver",
            countryState = CountryState(
                country = GraphQLCountry.Canada,
                province = GraphQLCanadianProvince.Provinces.Yukon.name
            ),
            postalCode = "23efa",
    )
}