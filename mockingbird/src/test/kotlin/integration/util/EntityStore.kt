package integration.util

import com.expediagroup.graphql.generator.execution.OptionalInput
import com.expediagroup.graphql.generator.scalars.ID
import com.leftindust.mockingbird.dao.entity.*
import com.leftindust.mockingbird.dao.entity.enums.Ethnicity
import com.leftindust.mockingbird.dao.entity.enums.Relationship
import com.leftindust.mockingbird.dao.entity.enums.Sex
import com.leftindust.mockingbird.graphql.types.*
import com.leftindust.mockingbird.graphql.types.input.GraphQLDateInput
import com.leftindust.mockingbird.graphql.types.input.GraphQLEmergencyContactInput
import com.leftindust.mockingbird.graphql.types.input.GraphQLEventInput
import com.leftindust.mockingbird.graphql.types.input.GraphQLPatientInput
import java.sql.Timestamp
import java.time.Duration

object EntityStore {
    fun patient(testName: String) = Patient(
        firstName = "marcus",
        middleName = testName,
        lastName = "dunn",
        dateOfBirth = Timestamp.valueOf("2020-01-02 09:01:15"),
        addresses = setOf(
            Address(
                type = GraphQLAddressType.Home,
                city = "North Vancouver",
                countryState = CountryState(
                    country = GraphQLCountry.Canada,
                    province = GraphQLCanadianProvince.Alberta.name
                ),
                address = "874 West 1st Street",
                postalCode = "y7h1p4",
            )
        ),
        emails = setOf(Email(email = "hello@world.ca", type = GraphQLEmailType.Personal)),
        phones = setOf(Phone(6632231111, GraphQLPhoneType.Home)),
        sex = Sex.Male,
        gender = Sex.Male.name,
        ethnicity = Ethnicity.White,
        insuranceNumber = "marcus",
        contacts = emptySet(),
        doctors = emptySet(),
    )

    fun doctor(testName: String) = Doctor(
        firstName = "dan",
        lastName = testName,
        middleName = "the man",
        dateOfBirth = Timestamp.valueOf("2018-01-02 09:01:15"),
        addresses = setOf(
            Address(
                type = GraphQLAddressType.Home,
                address = "999 East 7th Drive",
                city = "West Vancouver",
                countryState = CountryState(
                    country = GraphQLCountry.Canada,
                    province = GraphQLCanadianProvince.NewBrunswick.name
                ),
                postalCode = "y7h1p5",
            )
        ),
        emails = setOf(Email(email = "world@hello.ca", type = GraphQLEmailType.Personal)),
        phones = setOf(Phone(6632231211, GraphQLPhoneType.Home)),
        title = "sir",
        patients = emptySet(),
        schedule = Schedule(),
    )

    fun graphQLPatientInput(testName: String) = GraphQLPatientInput(
        firstName = OptionalInput.Defined("aydan"),
        middleName = OptionalInput.Defined(testName),
        lastName = OptionalInput.Defined("gaite"),
        phoneNumbers = listOf(
            GraphQLPhone(
                number = 11111111,
                type = GraphQLPhoneType.Work,
            )
        ),
        dateOfBirth = OptionalInput.Defined(
            GraphQLTimeInput(
                date = GraphQLDateInput(
                    day = 12,
                    month = GraphQLMonth.Apr,
                    year = 1948
                )
            )
        ),
        addresses = listOf(
            GraphQLAddress(
                addressType = GraphQLAddressType.Home,
                address = "6732 main st",
                city = "East Vancouver",
                country = GraphQLCountry.Canada,
                province = GraphQLCanadianProvince.NewBrunswick.name,
                postalCode = "h221234",
            )
        ),
        emails = listOf(
            GraphQLEmail(
                type = GraphQLEmailType.School,
                email = "hello@mars.ca",
            )
        ),
        insuranceNumber = OptionalInput.Defined(ID("111111111")),
        sex = OptionalInput.Defined(Sex.Male),
        ethnicity = OptionalInput.Defined(Ethnicity.AmericanAboriginal),
        emergencyContact = listOf(
            GraphQLEmergencyContactInput(
                firstName = "mom firstName",
                middleName = "mom middleName",
                lastName = "mom lastName",
                relationship = Relationship.Parent,
                phones = listOf(
                    GraphQLPhone(
                        number = 111111111,
                        type = GraphQLPhoneType.Work,
                    ),
                    GraphQLPhone(
                        number = 223223222,
                        type = GraphQLPhoneType.Home,
                    ),
                ),
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
        durationMillis = Duration.ofHours(1).toMillis(),
        doctors = emptySet(),
        patients = emptySet()
    )
}