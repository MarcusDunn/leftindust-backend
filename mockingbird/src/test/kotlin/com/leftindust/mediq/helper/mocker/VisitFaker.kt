package com.leftindust.mediq.helper.mocker

import com.leftindust.mediq.dao.entity.Visit
import com.leftindust.mediq.graphql.types.icd.FoundationIcdCode
import kotlin.math.absoluteValue

class VisitFaker(seed: Long, doctorPatientFaker: DoctorPatientFaker? = null) : MediqFaker<Visit>(seed) {
    val timestampFaker = TimestampFaker(seed)
    val patientFaker = doctorPatientFaker?.patientFaker ?: PatientFaker(seed)
    val doctorFaker = doctorPatientFaker?.doctorFaker ?: DoctorFaker(seed)

    override fun create(): Visit {
        val (timeBooked, timeOfVisit) = listOf(timestampFaker(), timestampFaker()).sortedBy { it.nanos }

        return Visit(
            timeBooked = timeBooked,
            timeOfVisit = timeOfVisit,
            patient = patientFaker(),
            doctor = doctorFaker(),
            title = faker.pokemon.names(),
            description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
            icdFoundationCode = FoundationIcdCode(seededRandom.nextLong().absoluteValue.toString()),
            )
    }
}
