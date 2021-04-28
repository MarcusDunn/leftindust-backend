package com.leftindust.mockingbird.dao.entity

import com.leftindust.mockingbird.dao.entity.enums.Relationship
import com.leftindust.mockingbird.dao.entity.superclasses.AbstractJpaPersistable
import com.leftindust.mockingbird.graphql.types.input.GraphQLEmergencyContactInput
import javax.persistence.*

@Entity(name = "emergency_contact")
class EmergencyContact(
    @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.MERGE])
    @JoinColumn(name = "patient_id", referencedColumnName = "id")
    var patient: Patient,
    @Enumerated(EnumType.STRING)
    var relationship: Relationship,
    @OneToOne(fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    @JoinColumn(name = "name_info_id", nullable = false)
    var nameInfo: NameInfo,
    @OneToMany(fetch = FetchType.EAGER, cascade = [CascadeType.ALL], orphanRemoval = true)
    var phone: Set<Phone> = emptySet()
) : AbstractJpaPersistable<Long>() {
    constructor(graphQLEmergencyContactInput: GraphQLEmergencyContactInput, patient: Patient) : this(
        patient = patient,
        relationship = graphQLEmergencyContactInput.relationship,
        nameInfo = NameInfo(
            firstName = graphQLEmergencyContactInput.firstName,
            middleName = graphQLEmergencyContactInput.middleName,
            lastName = graphQLEmergencyContactInput.lastName,
        ),
        phone = graphQLEmergencyContactInput.phones.map { Phone(it) }.toSet(),
    )
}