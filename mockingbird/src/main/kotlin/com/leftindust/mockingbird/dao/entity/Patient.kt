package com.leftindust.mockingbird.dao.entity

import com.expediagroup.graphql.execution.OptionalInput
import com.leftindust.mockingbird.dao.entity.enums.Ethnicity
import com.leftindust.mockingbird.dao.entity.enums.Sex
import com.leftindust.mockingbird.dao.entity.superclasses.Person
import com.leftindust.mockingbird.extensions.*
import com.leftindust.mockingbird.graphql.types.GraphQLPhoneType
import com.leftindust.mockingbird.graphql.types.GraphQLTime
import com.leftindust.mockingbird.graphql.types.input.GraphQLPatientInput
import java.sql.Timestamp
import javax.persistence.*

@Entity(name = "patient")
class Patient(
    firstName: String,
    middleName: String? = null,
    lastName: String,
    dateOfBirth: Timestamp? = null,
    address: String? = null,
    email: String? = null,
    cellPhone: String? = null,
    workPhone: String? = null,
    homePhone: String? = null,
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
) : Person(firstName, lastName, middleName, dateOfBirth, address, email, cellPhone, workPhone, homePhone) {
    @Throws(IllegalArgumentException::class)
    constructor(
        graphQLPatientInput: GraphQLPatientInput,
        doctors: Set<DoctorPatient>,
    ) : this(
        firstName = graphQLPatientInput.firstName
            .getOrThrow(IllegalArgumentException("firstName must be defined when constructing a Patient")),
        middleName = graphQLPatientInput.middleName
            .getOrNull(),
        lastName = graphQLPatientInput.lastName
            .getOrThrow(IllegalArgumentException("lastName must be defined when constructing a Patient")),
        dateOfBirth = graphQLPatientInput.dateOfBirth
            .getOrNull()
            ?.unixMilliseconds?.let { Timestamp(it) },
        address = graphQLPatientInput.address
            .getOrNull(),
        email = graphQLPatientInput.email
            .getOrNull(),
        insuranceNumber = graphQLPatientInput.insuranceNumber
            .getOrNull(),
        sex = graphQLPatientInput.sex
            .getOrThrow(IllegalArgumentException("sex must be defined when constructing a Patient")),
        gender = graphQLPatientInput.gender
            .getOrDefault(graphQLPatientInput.sex.getOrNull()!!.toString()),
        doctors = doctors,
        ethnicity = graphQLPatientInput.ethnicity
            .getOrNull()
    ) {
        cellPhone = graphQLPatientInput.phoneNumbers
            .getOrNull()
            ?.find { it.type == GraphQLPhoneType.Cell }
            ?.number?.toString()
        workPhone = graphQLPatientInput.phoneNumbers
            .getOrNull()
            ?.find { it.type == GraphQLPhoneType.Work }
            ?.number?.toString()
        homePhone = graphQLPatientInput.phoneNumbers.getOrNull()
            ?.find { it.type == GraphQLPhoneType.Home }
            ?.number?.toString()
        assert(graphQLPatientInput.pid is OptionalInput.Undefined) // check that they are not trying to assign primary key on creation
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Patient) return false

        if (firstName != other.firstName) return false
        if (middleName != other.middleName) return false
        if (lastName != other.lastName) return false
        if (dateOfBirth != other.dateOfBirth) return false
        if (address != other.address) return false
        if (email != other.email) return false
        if (cellPhone != other.cellPhone) return false
        if (workPhone != other.workPhone) return false
        if (homePhone != other.homePhone) return false
        if (insuranceNumber != other.insuranceNumber) return false
        if (contacts != other.contacts) return false
        if (doctors != other.doctors) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + firstName.hashCode()
        result = 31 * result + (middleName?.hashCode() ?: 0)
        result = 31 * result + lastName.hashCode()
        result = 31 * result + (dateOfBirth?.hashCode() ?: 0)
        result = 31 * result + (address?.hashCode() ?: 0)
        result = 31 * result + (email?.hashCode() ?: 0)
        result = 31 * result + (cellPhone?.hashCode() ?: 0)
        result = 31 * result + (workPhone?.hashCode() ?: 0)
        result = 31 * result + (homePhone?.hashCode() ?: 0)
        result = 31 * result + (insuranceNumber?.hashCode() ?: 0)
        result = 31 * result + contacts.hashCode()
        result = 31 * result + doctors.hashCode()
        return result
    }

    override fun toString(): String {
        return "Patient(firstName='$firstName', middleName=$middleName, lastName='$lastName', dateOfBirth=$dateOfBirth, address=$address, email=$email, cellPhone=$cellPhone, workPhone=$workPhone, homePhone=$homePhone, insuranceNumber=$insuranceNumber, contacts=$contacts, doctors=$doctors)"
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

        val onUndefined: GraphQLTime? = dateOfBirth?.let { GraphQLTime(it) }

        dateOfBirth = patientInput.dateOfBirth.onUndefined(onUndefined)?.toTimestamp()
        address = patientInput.address.onUndefined(address)
        email = patientInput.email.onUndefined(email)

        when (val phoneNumbers = patientInput.phoneNumbers) {
            is OptionalInput.Defined -> {
                if (phoneNumbers.value == null) {
                    cellPhone = null
                    workPhone = null
                    homePhone = null
                } else {
                    cellPhone = phoneNumbers.value!!
                        .find { it.type == GraphQLPhoneType.Cell }
                        .let { if (it == null) null else cellPhone }
                    workPhone = phoneNumbers.value!!
                        .find { it.type == GraphQLPhoneType.Work }
                        .let { if (it == null) null else workPhone }
                    homePhone = phoneNumbers.value!!
                        .find { it.type == GraphQLPhoneType.Home }
                        .let { if (it == null) null else homePhone }
                }
            }
            is OptionalInput.Undefined -> {/* no-op */
            }
        }

        insuranceNumber = patientInput.insuranceNumber.onUndefined(insuranceNumber)

        sex = patientInput.sex.onUndefined(sex) ?: throw IllegalArgumentException("sex cannot be set to null")

        gender =
            patientInput.gender.onUndefined(gender) ?: throw IllegalArgumentException("gender cannot be set to null")

        ethnicity = patientInput.ethnicity.onUndefined(ethnicity)
    }
}