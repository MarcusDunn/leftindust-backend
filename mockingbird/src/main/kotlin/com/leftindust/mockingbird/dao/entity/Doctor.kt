package com.leftindust.mockingbird.dao.entity

import biweekly.component.VEvent
import com.leftindust.mockingbird.dao.entity.superclasses.Person
import com.leftindust.mockingbird.graphql.types.input.GraphQLDoctorInput
import java.sql.Timestamp
import javax.persistence.*

@Entity
class Doctor(
    firstName: String,
    lastName: String,
    middleName: String? = null,
    dateOfBirth: Timestamp,
    addresses: Set<Address> = emptySet(),
    emails: Set<Email> = emptySet(),
    phones: Set<Phone> = emptySet(),
    user: MediqUser? = null,
    @Column(name = "title", nullable = true)
    val title: String? = null,
    @OneToMany(mappedBy = "doctor", cascade = [CascadeType.ALL])
    var patients: Set<DoctorPatient> = emptySet(),
    @Embedded
    var schedule: Schedule = Schedule()
) : Person(firstName, lastName, middleName, dateOfBirth, addresses, emails, phones, user) {
    constructor(graphQLDoctorInput: GraphQLDoctorInput, user: MediqUser?, patients: Collection<Patient>) : this(
        firstName = graphQLDoctorInput.firstName,
        lastName = graphQLDoctorInput.lastName,
        middleName = graphQLDoctorInput.middleName,
        dateOfBirth = graphQLDoctorInput.dateOfBirth.toTimestamp(),
        addresses = graphQLDoctorInput.addresses?.map { Address(it) }?.toSet() ?: emptySet(),
        emails = graphQLDoctorInput.emails?.map { Email(it) }?.toSet() ?: emptySet(),
        phones = graphQLDoctorInput.phones?.map { Phone(it) }?.toSet() ?: emptySet(),
        user = user,
        // patients handled in following block
        title = graphQLDoctorInput.title,
        schedule = Schedule(),
    ) {
        this.patients = patients.map { DoctorPatient(it, this) }.toSet()
    }

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
            summary = it.summary
            description = it.description
            dateStart = it.dateStart
            dateEnd = it.dateEnd
            dateTimeStamp = it.dateTimeStamp
            duration = it.duration
        }
    }
}










