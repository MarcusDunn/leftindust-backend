package com.leftindust.mockingbird.dao.entity

import biweekly.component.VEvent
import com.leftindust.mockingbird.dao.entity.superclasses.Person
import java.sql.Timestamp
import javax.persistence.*

@Entity
class Doctor(
    firstName: String,
    lastName: String,
    middleName: String? = null,
    dateOfBirth: Timestamp? = null,
    address: String? = null,
    email: String? = null,
    cellPhone: String? = null,
    workPhone: String? = null,
    homePhone: String? = null,
    @Column(name = "title", nullable = true)
    val title: String? = null,
    @Column(name = "pager_number", nullable = true)
    val pagerNumber: String? = null,
    @OneToMany(mappedBy = "doctor", cascade = [CascadeType.ALL])
    var patients: Set<DoctorPatient> = emptySet(),
    @Embedded
    val schedule: Schedule = Schedule()
) : Person(firstName, lastName, middleName, dateOfBirth, address, email, cellPhone, workPhone, homePhone) {

    fun addPatient(patient: Patient): Doctor {
        val doctorPatient = DoctorPatient(doctor = this, patient = patient)
        patient.doctors = patient.doctors.toMutableSet().apply { add(doctorPatient) }
        this.patients = this.patients.toMutableSet().apply { add(doctorPatient) }
        return this
    }

    fun getEventsBetween(from: Timestamp, to: Timestamp): List<DocVEvent> {
        // TODO: 2021-03-13  remove this is favor of calling directly on schedule
        return this.schedule.getEventsBetween(from, to).map { DocVEvent(this, it) }
    }

    data class DocVEvent(val doctor: Doctor) : VEvent() {
        constructor(doctor: Doctor, it: VEvent) : this(doctor) {
            this.summary = it.summary
            this.description = it.description
            this.dateStart = it.dateStart
            this.dateEnd = it.dateEnd
            this.dateTimeStamp = it.dateTimeStamp
            this.duration = it.duration
        }
    }
}










