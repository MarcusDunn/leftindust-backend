package com.leftindust.mockingbird.dao.entity

import com.expediagroup.graphql.generator.execution.OptionalInput
import com.leftindust.mockingbird.dao.entity.superclasses.Person
import com.leftindust.mockingbird.extensions.replaceAll
import com.leftindust.mockingbird.extensions.replaceAllIfNotNull
import com.leftindust.mockingbird.extensions.toLong
import com.leftindust.mockingbird.graphql.types.input.GraphQLDoctorEditInput
import com.leftindust.mockingbird.graphql.types.input.GraphQLDoctorInput
import org.hibernate.Session
import java.sql.Date
import javax.persistence.*

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
    @ManyToOne
    @JoinColumn(name = "clinic_id", nullable = true)
    var clinic: Clinic? = null,
    @OneToMany(mappedBy = "doctor", cascade = [CascadeType.ALL])
    var patients: MutableSet<DoctorPatient> = mutableSetOf(),
) : Person(nameInfo, addresses.toMutableSet(), emails.toMutableSet(), phones.toMutableSet(), user, schedule) {
    constructor(
        graphQLDoctorInput: GraphQLDoctorInput,
        user: MediqUser?,
        patients: Collection<Patient>,
        clinic: Clinic? = null
    ) : this(
        nameInfo = NameInfo(graphQLDoctorInput.nameInfo),
        dateOfBirth = graphQLDoctorInput.dateOfBirth?.toDate(),
        addresses = graphQLDoctorInput.addresses?.map { Address(it) }?.toSet() ?: emptySet(),
        emails = graphQLDoctorInput.emails?.map { Email(it) }?.toSet() ?: emptySet(),
        phones = graphQLDoctorInput.phones?.map { Phone(it) }?.toSet() ?: emptySet(),
        user = user,
        clinic = clinic,
        title = graphQLDoctorInput.title,
    ) {
        patients.forEach { it.addDoctor(this) }
    }

    fun addPatient(patient: Patient): Doctor {
        val doctorPatient = DoctorPatient(doctor = this, patient = patient)
        patient.doctors.add(doctorPatient)
        this.patients.add(doctorPatient)
        return this
    }

    fun setByGqlInput(graphQLDoctorEditInput: GraphQLDoctorEditInput, session: Session, newUser: MediqUser? = null) {
        nameInfo.setByGqlInput(graphQLDoctorEditInput.nameInfo)
        dateOfBirth = graphQLDoctorEditInput.dateOfBirth?.toDate() ?: dateOfBirth
        address.replaceAllIfNotNull(graphQLDoctorEditInput.addresses?.map { Address(it) }?.toSet())
        email.replaceAllIfNotNull(graphQLDoctorEditInput.emails?.map { Email(it) }?.toSet())
        phone.replaceAllIfNotNull(graphQLDoctorEditInput.phones?.map { Phone(it) }?.toSet() ?: phone)
        user = newUser ?: user
        title = graphQLDoctorEditInput.title ?: title
        clinic = when (val optionalInput = graphQLDoctorEditInput.clinic) {
            OptionalInput.Undefined -> clinic
            is OptionalInput.Defined -> optionalInput.value?.let { session.get(Clinic::class.java, it.id) }
            null -> null
        }
        if (graphQLDoctorEditInput.patients != null) {
            for (doctorPatient in patients) {
                doctorPatient.patient.doctors.removeIf { it.doctor.id == this.id }
            }
            patients.clear()
            graphQLDoctorEditInput.patients.map { session.get(Patient::class.java, it.id).addDoctor(this) }
        }
    }
}






