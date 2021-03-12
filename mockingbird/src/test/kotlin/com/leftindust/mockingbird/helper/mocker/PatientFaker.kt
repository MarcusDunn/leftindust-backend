package com.leftindust.mockingbird.helper.mocker

import com.leftindust.mockingbird.dao.entity.Patient
import com.leftindust.mockingbird.dao.entity.enums.Sex
import kotlin.math.absoluteValue
import kotlin.random.asKotlinRandom

class PatientFaker(
    seed: Long,
    private val doctorPatientFaker: DoctorPatientFaker? = null,
    private val contactFaker: ContactFaker? = null
) : MediqFaker<Patient>(seed) {
    private val numberFaker = NumberFaker(seed)
    private val middleNameFaker = MiddleNameFaker(seed)
    private val timestampFaker = TimestampFaker(seed)
    private val emailFaker = EmailFaker(seed)
    private val phoneFaker = PhoneFaker(seed)

    override fun create(): Patient {
        val sex = Sex.values().random(seededRandom.asKotlinRandom())
        return Patient(
            firstName = faker.name.firstName(),
            middleName = middleNameFaker perhapsNullWithOddsOf 50,
            lastName = faker.name.lastName(),
            dateOfBirth = timestampFaker atLeastYearsAgo 12,
            address = faker.address.fullAddress(),
            email = emailFaker(),
            insuranceNumber = seededRandom.nextLong().absoluteValue.toString().slice(0..10),
            cellPhone = phoneFaker(),
            workPhone = phoneFaker(),
            doctors = doctorPatientFaker?.let { nnDPF -> (0..seededRandom.nextInt(4)).map { nnDPF() }.toSet() }
                ?: emptySet(),
            contacts = contactFaker?.let { setOf(it(), it()) } ?: emptySet(),
            sex = sex,
            gender = sex.toString(),
        ).apply { this.contacts.map { it.patient = this } }
    }
}