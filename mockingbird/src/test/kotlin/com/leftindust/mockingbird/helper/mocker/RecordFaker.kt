package com.leftindust.mockingbird.helper.mocker

import com.google.gson.JsonObject
import com.leftindust.mockingbird.dao.entity.Patient
import com.leftindust.mockingbird.dao.entity.MediqRecord
import com.leftindust.mockingbird.dao.entity.enums.RecordType
import kotlin.random.asKotlinRandom

class RecordFaker(seed: Long) : MediqFaker<MediqRecord>(seed) {
    private val patientFaker = PatientFaker(seed)
    private val timestampFaker = TimestampFaker(seed)
    private val numberFaker = NumberFaker(seed)

    override fun create() = create(patient = patientFaker())

    fun create(patient: Patient) = MediqRecord(
        rid = numberFaker(),
        patient = patient,
        creationDate = timestampFaker(),
        type = RecordType.values().random(seededRandom.asKotlinRandom()),
        jsonBlob = JsonObject().toString(),
    )
}

