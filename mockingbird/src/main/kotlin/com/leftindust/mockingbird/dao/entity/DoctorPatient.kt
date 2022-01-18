package com.leftindust.mockingbird.dao.entity

import com.leftindust.mockingbird.dao.entity.superclasses.AbstractJpaPersistable
import java.sql.Timestamp
import java.time.Instant
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.ManyToOne
import javax.persistence.Table
import javax.persistence.UniqueConstraint

// note that for the sake of persisting updates we treat the doctor as owning this table.
@Entity
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["patient", "doctor"])])
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