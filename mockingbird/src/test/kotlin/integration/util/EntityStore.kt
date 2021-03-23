package integration.util

import com.leftindust.mockingbird.dao.entity.Patient
import com.leftindust.mockingbird.dao.entity.enums.Ethnicity
import com.leftindust.mockingbird.dao.entity.enums.Sex
import java.sql.Timestamp

object EntityStore {
    fun patient() = Patient(
        firstName = "marcus",
        middleName = "hello",
        lastName = "dunn",
        dateOfBirth = Timestamp.valueOf("2020-01-02 09:01:15"),
        address = "3521 West 1st Street",
        emails = listOf("hello@world.ca"),
        cellPhone = "6632231111",
        workPhone = "1234567890",
        homePhone = "1234567890",
        sex = Sex.Male,
        gender = Sex.Male.name,
        ethnicity = Ethnicity.White,
        insuranceNumber = "marcus",
        contacts = emptySet(),
        doctors = emptySet(),
    )
}