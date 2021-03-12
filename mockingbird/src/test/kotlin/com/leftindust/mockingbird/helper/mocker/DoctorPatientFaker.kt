package com.leftindust.mockingbird.helper.mocker

import com.leftindust.mockingbird.dao.entity.DoctorPatient

class DoctorPatientFaker(seed: Long) : MediqFaker<DoctorPatient>(seed) {
    val doctorFaker = DoctorFaker(seed)
    val patientFaker = PatientFaker(seed)

    override fun create(): DoctorPatient {
        return DoctorPatient(
            doctor = doctorFaker(),
            patient = patientFaker()
        )
    }
}