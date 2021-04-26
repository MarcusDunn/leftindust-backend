package com.leftindust.mockingbird.dao.entity

import com.leftindust.mockingbird.dao.entity.superclasses.Person
import com.leftindust.mockingbird.graphql.types.input.GraphQLDoctorInput
import java.sql.Timestamp
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.OneToMany

@Entity
class Doctor(
    nameInfo: NameInfo,
    dateOfBirth: Timestamp,
    addresses: Set<Address> = emptySet(),
    emails: Set<Email> = emptySet(),
    phones: Set<Phone> = emptySet(),
    user: MediqUser? = null,
    schedule: Schedule = Schedule(),
    @Column(name = "title", nullable = true)
    val title: String? = null,
    @OneToMany(mappedBy = "doctor", cascade = [CascadeType.ALL])
    var patients: Set<DoctorPatient> = emptySet(),
) : Person(nameInfo, dateOfBirth, addresses, emails, phones, user, schedule) {
    constructor(graphQLDoctorInput: GraphQLDoctorInput, user: MediqUser?, patients: Collection<Patient>) : this(
        nameInfo = NameInfo(graphQLDoctorInput.nameInfo),
        dateOfBirth = graphQLDoctorInput.dateOfBirth.toTimestamp(),
        addresses = graphQLDoctorInput.addresses?.map { Address(it) }?.toSet() ?: emptySet(),
        emails = graphQLDoctorInput.emails?.map { Email(it) }?.toSet() ?: emptySet(),
        phones = graphQLDoctorInput.phones?.map { Phone(it) }?.toSet() ?: emptySet(),
        user = user,
        // patients handled in following block
        title = graphQLDoctorInput.title,
    ) {
        this.patients = patients.map { DoctorPatient(it, this) }.toSet()
    }

    fun addPatient(patient: Patient): Doctor {
        val doctorPatient = DoctorPatient(doctor = this, patient = patient)
        patient.doctors = patient.doctors.toMutableSet().apply { add(doctorPatient) }
        this.patients = this.patients.toMutableSet().apply { add(doctorPatient) }
        return this
    }
}










