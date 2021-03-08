package com.leftindust.mediq.helper.mocker

import com.leftindust.mediq.dao.entity.EmergencyContact
import com.leftindust.mediq.dao.entity.Patient
import com.leftindust.mediq.dao.entity.enums.Relationship
import kotlin.math.absoluteValue
import kotlin.random.asKotlinRandom

class ContactFaker(seed: Long) : MediqFaker<EmergencyContact>(seed) {
    private val patientFaker = PatientFaker(seed)
    private val numberFaker = NumberFaker(seed)

    fun create(patient: Patient): EmergencyContact {
        return EmergencyContact(
            cid = numberFaker().absoluteValue.toLong(),
            patient = patient,
            firstName = faker.name.firstName(),
            middleName = faker.ancient.titan(),
            lastName = faker.name.lastName(),
            cellNumber = seededRandom.nextLong().toString().slice(0..10).toLong().absoluteValue,
            homeNumber = seededRandom.nextLong().toString().slice(0..10).toLong().absoluteValue,
            workNumber = seededRandom.nextLong().toString().slice(0..10).toLong().absoluteValue,
            relationship = Relationship.values().random(seededRandom.asKotlinRandom())
        )
    }

    override fun create(): EmergencyContact {
        return create(patientFaker())
    }

}
