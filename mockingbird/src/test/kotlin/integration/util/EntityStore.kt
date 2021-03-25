package integration.util

import com.leftindust.mockingbird.dao.entity.*
import com.leftindust.mockingbird.dao.entity.enums.Ethnicity
import com.leftindust.mockingbird.dao.entity.enums.Sex
import com.leftindust.mockingbird.graphql.types.GraphQLEmailType
import com.leftindust.mockingbird.graphql.types.GraphQLPhoneType
import java.sql.Timestamp

object EntityStore {
    fun patient() = Patient(
        firstName = "marcus",
        middleName = "hello",
        lastName = "dunn",
        dateOfBirth = Timestamp.valueOf("2020-01-02 09:01:15"),
        address = "3521 West 1st Street",
        emails = setOf(Email(email = "hello@world.ca", type = GraphQLEmailType.Personal)),
        phones = setOf(Phone(6632231111, GraphQLPhoneType.Home)),
        sex = Sex.Male,
        gender = Sex.Male.name,
        ethnicity = Ethnicity.White,
        insuranceNumber = "marcus",
        contacts = emptySet(),
        doctors = emptySet(),
    )

    fun doctor() = Doctor(
        firstName = "dan",
        lastName = "shervani",
        middleName = "the man",
        dateOfBirth = Timestamp.valueOf("2018-01-02 09:01:15"),
        address = "572 East 2nd Street",
        emails = setOf(Email(email = "world@hello.ca", type = GraphQLEmailType.Personal)),
        phones = setOf(Phone(6632231211, GraphQLPhoneType.Home)),
        title = "sir",
        patients = emptySet(),
        schedule = Schedule(),
    )
}