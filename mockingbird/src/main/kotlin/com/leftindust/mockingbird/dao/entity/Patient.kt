package com.leftindust.mockingbird.dao.entity

import com.expediagroup.graphql.generator.execution.OptionalInput
import com.expediagroup.graphql.generator.scalars.ID
import com.leftindust.mockingbird.dao.entity.enums.Ethnicity
import com.leftindust.mockingbird.dao.entity.enums.Sex
import com.leftindust.mockingbird.dao.entity.superclasses.Person
import com.leftindust.mockingbird.extensions.*
import com.leftindust.mockingbird.graphql.types.GraphQLTimeInput
import com.leftindust.mockingbird.graphql.types.input.GraphQLPatientInput
import org.hibernate.Session
import java.sql.Timestamp
import javax.persistence.*

@Entity(name = "patient")
class Patient(
    firstName: String,
    middleName: String? = null,
    lastName: String,
    dateOfBirth: Timestamp,
    addresses: Set<Address> = emptySet(),
    emails: Set<Email> = emptySet(),
    phones: Set<Phone> = emptySet(),
    @Column(name = "sex", nullable = false)
    @Enumerated(EnumType.STRING)
    var sex: Sex,
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
    @OneToMany(mappedBy = "patient", fetch = FetchType.LAZY)
    var doctors: Set<DoctorPatient> = emptySet(),
) : Person(firstName, lastName, middleName, dateOfBirth, addresses, emails, phones) {

    @Throws(IllegalArgumentException::class)
    constructor(
        graphQLPatientInput: GraphQLPatientInput,
        session: Session
    ) : this(
        firstName = graphQLPatientInput.firstName
            .getOrThrow(IllegalArgumentException("firstName must be defined when constructing a Patient")),
        middleName = graphQLPatientInput.middleName
            .getOrNull(),
        lastName = graphQLPatientInput.lastName
            .getOrThrow(IllegalArgumentException("lastName must be defined when constructing a Patient")),
        dateOfBirth = graphQLPatientInput.dateOfBirth
            .getOrThrow(IllegalArgumentException("date of birth must be defined"))
            .toTimestamp(),
        addresses = graphQLPatientInput.addresses
            .getOrDefault(emptyList()).map { Address(it) }.toSet(),
        emails = graphQLPatientInput.emails
            .getOrNull()?.map { Email(it) }?.toSet() ?: emptySet(),
        insuranceNumber = graphQLPatientInput.insuranceNumber
            .getOrNull()?.value,
        sex = graphQLPatientInput.sex
            .getOrThrow(IllegalArgumentException("sex must be defined when constructing a Patient")),
        gender = graphQLPatientInput.gender
            .getOrDefault(graphQLPatientInput.sex.getOrNull()!!.toString()),
        doctors = emptySet<DoctorPatient>(), // set after constructor is called
        ethnicity = graphQLPatientInput.ethnicity
            .getOrNull(),
    ) {
        phone = graphQLPatientInput.phoneNumbers
            .getOrNull()
            ?.map { Phone(it) }
            ?.toSet()
            ?: emptySet()

        graphQLPatientInput.doctors
            .getOrDefault(emptySet())
            .forEach { did ->
                addDoctor(
                    session.get(Doctor::class.java, did.value.toLong())
                        ?: throw IllegalArgumentException("could not find doctor with did: ${did.value}")
                )
            }

        contacts = graphQLPatientInput.emergencyContact
            .getOrDefault(emptySet())
            .map { EmergencyContact(it, this) }
            .toSet()


        // check that they are not trying to assign primary key on creation
        if (graphQLPatientInput.pid !is OptionalInput.Undefined) {
            throw IllegalArgumentException("cannot assign a pid to a newly created patient, let the server handle that!")
        }
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
                    PID -> "id" // TODO: 2021-03-11  
                    FIRST_NAME -> "firstName"
                    LAST_NAME -> "lastName"
                }
            }

        fun instanceValue(receiver: Patient): String {
            return when (this) {
                PID -> receiver.id!!.toString()
                FIRST_NAME -> receiver.firstName
                LAST_NAME -> receiver.lastName
            }
        }
    }


    @Throws(IllegalArgumentException::class)
    fun setByGqlInput(patientInput: GraphQLPatientInput) {
        if (patientInput.pid.getOrNull()?.toLong() != this.id!!)
            throw IllegalArgumentException("pid does not match entity, expected ${this.id} got ${patientInput.pid.getOrNull()}")

        firstName = patientInput.firstName
            .onUndefined(firstName) ?: throw IllegalArgumentException("firstName cannot be set to null")
        middleName = patientInput.middleName.onUndefined(middleName)

        lastName = patientInput.lastName
            .onUndefined(firstName) ?: throw IllegalArgumentException("lastname cannot be set to null")

        dateOfBirth = (patientInput.dateOfBirth.onUndefined(GraphQLTimeInput(dateOfBirth))
            ?: throw IllegalArgumentException("date of birth cannot be set to null"))
            .toTimestamp()

        address = when (patientInput.addresses) {
            OptionalInput.Undefined -> address
            is OptionalInput.Defined -> patientInput.addresses.value
                ?.map { Address(it) }
                ?.toSet()
                ?: emptySet()
        }

        email = when (patientInput.emails) {
            OptionalInput.Undefined -> email
            is OptionalInput.Defined -> patientInput.emails.value
                ?.map { Email(it) }
                ?.toSet()
                ?: emptySet()
        }

        phone = when (patientInput.phoneNumbers) {
            OptionalInput.Undefined -> phone
            is OptionalInput.Defined -> patientInput.phoneNumbers.value
                ?.map { Phone(it) }
                ?.toSet()
                ?: emptySet()
        }

        insuranceNumber = patientInput.insuranceNumber.onUndefined(insuranceNumber?.let { ID(it) })?.value
        sex = patientInput.sex.onUndefined(sex)
            ?: throw IllegalArgumentException("sex cannot be set to null")
        gender = patientInput.gender.onUndefined(gender)
            ?: throw IllegalArgumentException("gender cannot be set to null")
        ethnicity = patientInput.ethnicity.onUndefined(ethnicity)
        doctors = when (patientInput.doctors) {
            is OptionalInput.Undefined -> doctors
            is OptionalInput.Defined -> TODO("waiting for aidan to need this")
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

    override fun toString(): String {
        return "Patient(sex=$sex, gender='$gender', ethnicity=$ethnicity, insuranceNumber=$insuranceNumber, contacts=$contacts, doctors=$doctors) ${super.toString()}"
    }
}