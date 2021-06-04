package com.leftindust.mockingbird.dao.entity

import com.expediagroup.graphql.generator.scalars.ID
import com.leftindust.mockingbird.dao.entity.enums.Ethnicity
import com.leftindust.mockingbird.dao.entity.enums.Sex
import com.leftindust.mockingbird.dao.entity.superclasses.Person
import com.leftindust.mockingbird.extensions.onUndefined
import com.leftindust.mockingbird.extensions.replaceAllIfNotNull
import com.leftindust.mockingbird.graphql.types.input.GraphQLPatientEditInput
import com.leftindust.mockingbird.graphql.types.input.GraphQLPatientInput
import org.hibernate.Session
import java.sql.Date
import javax.persistence.*

@Entity(name = "patient")
class Patient(
    nameInfo: NameInfo,
    addresses: Set<Address> = emptySet(),
    emails: Set<Email> = emptySet(),
    phones: Set<Phone> = emptySet(),
    schedule: Set<Event> = emptySet(),
    user: MediqUser? = null,
    @Column(name = "sex", nullable = false)
    @Enumerated(EnumType.STRING)
    var sex: Sex,
    @Column(name = "date_of_birth", nullable = false)
    var dateOfBirth: Date,
    @Column(name = "gender", nullable = false)
    var gender: String = sex.toString(),
    @Column(name = "ethnicity", nullable = true)
    @Enumerated(EnumType.STRING)
    var ethnicity: Ethnicity? = null,
    @Column(name = "insurance_number", nullable = true)
    var insuranceNumber: String? = null,
    @OneToMany(
        mappedBy = "patient",
        cascade = [CascadeType.ALL],
        fetch = FetchType.LAZY,
    )
    var contacts: Set<EmergencyContact> = emptySet(),
    @OneToMany(mappedBy = "patient", fetch = FetchType.LAZY, orphanRemoval = true)
    var doctors: MutableSet<DoctorPatient> = mutableSetOf(),
) : Person(
    nameInfo,
    addresses.toMutableSet(),
    emails.toMutableSet(),
    phones.toMutableSet(),
    user,
    schedule.toMutableSet()
) {

    /**
     * see [GraphQLPatientInput] for details on how updates should behave
     */
    constructor(
        graphQLPatientInput: GraphQLPatientInput,
        session: Session
    ) : this(
        nameInfo = NameInfo(graphQLPatientInput.nameInfo),
        dateOfBirth = graphQLPatientInput.dateOfBirth.toDate(),
        insuranceNumber = graphQLPatientInput.insuranceNumber?.value,
        sex = graphQLPatientInput.sex,
        gender = graphQLPatientInput.gender ?: graphQLPatientInput.sex.toString(),
        ethnicity = graphQLPatientInput.ethnicity,
        phones = graphQLPatientInput.phones?.map { Phone(it) }?.toSet() ?: emptySet(),
        addresses = graphQLPatientInput.addresses?.map { Address(it) }?.toSet() ?: emptySet(),
        emails = graphQLPatientInput.emails?.map { Email(it) }?.toSet() ?: emptySet(),
    ) {

        contacts = graphQLPatientInput.emergencyContacts?.map { EmergencyContact(it, this) }?.toSet() ?: emptySet()

        graphQLPatientInput.doctors
            ?.map { did -> session.get(Doctor::class.java, did.id) }
            ?.forEach { it.addPatient(this) }
    }

    fun addDoctor(doctor: Doctor): Patient {
        doctor.addPatient(this)
        return this
    }

    enum class SortableField {
        PID,
        FIRST_NAME,
        LAST_NAME,
        ;

        val fieldName: String
            get() {
                return when (this) {
                    PID -> Patient_.ID
                    FIRST_NAME -> "${Patient_.NAME_INFO}.${NameInfo_.FIRST_NAME}"
                    LAST_NAME -> "${Patient_.NAME_INFO}.${NameInfo_.LAST_NAME}"
                }
            }

        fun instanceValue(receiver: Patient): String {
            return when (this) {
                PID -> receiver.id!!.toString()
                FIRST_NAME -> receiver.nameInfo.firstName
                LAST_NAME -> receiver.nameInfo.firstName
            }
        }
    }


    @Throws(IllegalArgumentException::class)
    fun setByGqlInput(patientInput: GraphQLPatientEditInput, session: Session) {
        if (patientInput.pid.id != this.id) throw IllegalArgumentException("pid does not match entity, expected ${this.id} got ${patientInput.pid}")
        nameInfo.setByGqlInput(patientInput.nameInfo)
        dateOfBirth = patientInput.dateOfBirth?.toDate() ?: dateOfBirth
        address.replaceAllIfNotNull(patientInput.addresses?.map { Address(it) }?.toSet())
        email.replaceAllIfNotNull(patientInput.emails?.map { Email(it) }?.toSet())
        phone.replaceAllIfNotNull(patientInput.phones?.map { Phone(it) }?.toSet())
        insuranceNumber = patientInput.insuranceNumber.onUndefined(insuranceNumber?.let { ID(it) })?.value
        sex = patientInput.sex ?: sex
        gender = patientInput.gender ?: gender
        ethnicity = patientInput.ethnicity.onUndefined(ethnicity)

        if (patientInput.doctors != null) {
            doctors
                .flatMap { doctorPatient -> doctorPatient.doctor.patients.filter { it.patient.id == this.id } }
                .forEach {
                    it.removeFromLists()
                    session.delete(it)
                }

            assert(this.doctors.isEmpty())

            patientInput.doctors
                .map { session.get(Doctor::class.java, it.id) }
                .forEach { it.addPatient(this) }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as Patient

        if (sex != other.sex) return false
        if (gender != other.gender) return false
        if (ethnicity != other.ethnicity) return false
        if (insuranceNumber != other.insuranceNumber) return false
        if (contacts != other.contacts) return false
        if (doctors != other.doctors) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + sex.hashCode()
        result = 31 * result + gender.hashCode()
        result = 31 * result + (ethnicity?.hashCode() ?: 0)
        result = 31 * result + (insuranceNumber?.hashCode() ?: 0)
        result = 31 * result + contacts.hashCode()
        result = 31 * result + doctors.hashCode()
        return result
    }
}