package com.leftindust.mockingbird.dao.entity

import com.leftindust.mockingbird.dao.entity.superclasses.Person
import com.leftindust.mockingbird.extensions.toLong
import com.leftindust.mockingbird.graphql.types.input.GraphQLDoctorEditInput
import com.leftindust.mockingbird.graphql.types.input.GraphQLDoctorInput
import org.hibernate.Session
import java.sql.Date
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.OneToMany

@Entity
class Doctor(
    nameInfo: NameInfo,
    addresses: Set<Address> = emptySet(),
    emails: Set<Email> = emptySet(),
    phones: Set<Phone> = emptySet(),
    user: MediqUser? = null,
    schedule: Schedule = Schedule(),
    @Column(name = "title", nullable = true)
    var title: String? = null,
    @Column(name = "date_of_birth", nullable = true)
    var dateOfBirth: Date? = null,
    @OneToMany(mappedBy = "doctor", cascade = [CascadeType.ALL])
    var patients: Set<DoctorPatient> = emptySet(),
) : Person(nameInfo, addresses, emails, phones, user, schedule) {
    constructor(graphQLDoctorInput: GraphQLDoctorInput, user: MediqUser?, patients: Collection<Patient>) : this(
        nameInfo = NameInfo(graphQLDoctorInput.nameInfo),
        dateOfBirth = graphQLDoctorInput.dateOfBirth?.toDate(),
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

    private fun clearPatients() {
        for (doctorPatient in patients) {
            doctorPatient.patient.doctors = doctorPatient.patient.doctors.toMutableSet().apply {
                removeIf { it.doctor.id == this@Doctor.id }
            }
            this.patients = emptySet()
        }
    }

    fun setByGqlInput(graphQLDoctorEditInput: GraphQLDoctorEditInput, session: Session, new_user: MediqUser? = null) {
        nameInfo.setByGqlInput(graphQLDoctorEditInput.nameInfo)
        dateOfBirth = graphQLDoctorEditInput.dateOfBirth?.toDate() ?: dateOfBirth
        address = graphQLDoctorEditInput.addresses?.map { Address(it) }?.toSet() ?: address
        email = graphQLDoctorEditInput.emails?.map { Email(it) }?.toSet() ?: email
        phone = graphQLDoctorEditInput.phones?.map { Phone(it) }?.toSet() ?: phone
        user = new_user ?: user
        title = graphQLDoctorEditInput.title ?: title
        if (graphQLDoctorEditInput.patients != null) {
            clearPatients()
            graphQLDoctorEditInput.patients.map { session.get(Patient::class.java, it.toLong()).addDoctor(this) }
        }
    }
}










