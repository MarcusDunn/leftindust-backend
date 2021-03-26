package com.leftindust.mockingbird.dao.entity

import com.leftindust.mockingbird.dao.entity.enums.Relationship
import com.leftindust.mockingbird.dao.entity.superclasses.AbstractJpaPersistable
import com.leftindust.mockingbird.graphql.types.GraphQLEmergencyContact
import javax.persistence.*

@Entity(name = "emergency_contact")
class EmergencyContact(
    @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.MERGE])
    @JoinColumn(name = "patient_id", referencedColumnName = "id")
    var patient: Patient,
    @Enumerated(EnumType.STRING)
    var relationship: Relationship,
    @Column(name = "first_name", nullable = false)
    var firstName: String,
    @Column(name = "middle_name", nullable = true)
    var middleName: String? = null,
    @Column(name = "last_name", nullable = false)
    var lastName: String,
    @Column(name = "phone_numbers")
    @OneToMany
    var phones: Set<Phone> = emptySet()
) : AbstractJpaPersistable<Long>() {
    constructor(graphQLEmergencyContact: GraphQLEmergencyContact, patient: Patient) : this(
        patient,
        graphQLEmergencyContact.relationship,
        graphQLEmergencyContact.firstName,
        graphQLEmergencyContact.middleName,
        graphQLEmergencyContact.lastName,
        graphQLEmergencyContact.phoneNumbers.map { Phone(it) }.toSet(),
    )
}

