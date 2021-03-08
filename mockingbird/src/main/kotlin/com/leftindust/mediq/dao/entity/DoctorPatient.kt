package com.leftindust.mediq.dao.entity

import com.leftindust.mediq.dao.entity.superclasses.AbstractJpaPersistable
import java.sql.Timestamp
import java.time.Instant
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.ManyToOne

// note that for the sake of persisting updates we treat the doctor as owning this table.
@Entity(name = "doctor_patient")
class DoctorPatient(
    @ManyToOne(fetch = FetchType.LAZY)
    var patient: Patient,
    @ManyToOne(fetch = FetchType.LAZY)
    val doctor: Doctor,
    @Column(name = "date_created")
    val dateCreated: Timestamp = Timestamp.from(Instant.now())
) : AbstractJpaPersistable<Long>()