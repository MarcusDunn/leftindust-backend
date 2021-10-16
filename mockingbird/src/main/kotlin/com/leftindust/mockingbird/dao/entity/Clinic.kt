package com.leftindust.mockingbird.dao.entity

import com.leftindust.mockingbird.dao.entity.superclasses.AbstractJpaPersistable
import com.leftindust.mockingbird.graphql.types.input.GraphQLClinicEditInput
import com.leftindust.mockingbird.graphql.types.input.GraphQLClinicInput
import org.hibernate.Session
import javax.persistence.*

@Entity
class Clinic(
    @Column(nullable = false)
    var name: String,
    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(nullable = false)
    var address: Address,
    @ManyToMany(mappedBy = "clinics")
    var doctors: MutableSet<Doctor>,
) : AbstractJpaPersistable() {
    fun setByGqlInput(clinic: GraphQLClinicEditInput, session: Session) {
        name = clinic.name ?: name
        clinic.address?.let { address.setByGqlInput(it) }
        doctors = clinic.doctors?.map { session.get(Doctor::class.java, it.id) }?.toMutableSet() ?: doctors
    }

    constructor(gqlClinicInput: GraphQLClinicInput, session: Session) : this(
        name = gqlClinicInput.name,
        address = Address(gqlClinicInput.address),
        doctors = gqlClinicInput.doctors?.map { session.get(Doctor::class.java, it.id) }?.toMutableSet()
            ?: mutableSetOf(),
    )
}