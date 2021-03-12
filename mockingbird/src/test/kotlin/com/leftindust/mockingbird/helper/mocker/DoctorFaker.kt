package com.leftindust.mockingbird.helper.mocker

import com.leftindust.mockingbird.dao.entity.Doctor
import com.leftindust.mockingbird.dao.entity.Schedule

class DoctorFaker(seed: Long, private val doctorPatientFaker: DoctorPatientFaker? = null) : MediqFaker<Doctor>(seed) {
    private val numberFaker = NumberFaker(seed)
    private val titles = listOf("Grand", "Elated", "Top", "Bottom", "Lord", "Sir")
    private val timestampFaker = TimestampFaker(seed)
    private val emailFaker = EmailFaker(seed)
    private val phoneFaker = PhoneFaker(seed)

    override fun create() = Doctor(
        firstName = faker.name.firstName(),
        middleName = faker.lordOfTheRings.characters(),
        lastName = faker.name.lastName(),
        title = titles[seededRandom.nextInt(titles.size)],
        dateOfBirth = timestampFaker(),
        address = faker.address.fullAddress(),
        email = emailFaker(),
        cellPhone = phoneFaker(),
        workPhone = phoneFaker(),
        pagerNumber = phoneFaker(),
        patients = doctorPatientFaker?.let { nnDPF -> (0..seededRandom.nextInt(4)).map { nnDPF() }.toSet() }
            ?: emptySet(),
        schedule = Schedule(),
    )
}