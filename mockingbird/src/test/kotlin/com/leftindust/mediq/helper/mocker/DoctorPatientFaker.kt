package com.leftindust.mediq.helper.mocker

import com.leftindust.mediq.dao.entity.DoctorPatient

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