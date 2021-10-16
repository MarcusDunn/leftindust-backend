package com.leftindust.mockingbird.dao.entity

import com.leftindust.mockingbird.dao.entity.superclasses.AbstractJpaPersistable
import org.hibernate.annotations.Check
import java.sql.Timestamp
import java.time.Instant
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.ManyToOne

// note that for the sake of persisting updates we treat the doctor as owning this table.
@Entity
@Check(constraints = "UNIQUE(patient_id, doctor_id)")
class DoctorPatient(
    @ManyToOne(fetch = FetchType.LAZY)
    var patient: Patient,
    @ManyToOne(fetch = FetchType.LAZY)
    val doctor: Doctor,
    val dateCreated: Timestamp = Timestamp.from(Instant.now())
) : AbstractJpaPersistable() {
    fun removeFromLists() {
        patient.doctors.remove(this)
        doctor.patients.remove(this)
    }
}