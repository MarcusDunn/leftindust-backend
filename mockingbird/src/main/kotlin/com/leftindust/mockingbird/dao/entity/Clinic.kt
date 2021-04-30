package com.leftindust.mockingbird.dao.entity

import com.leftindust.mockingbird.dao.entity.superclasses.AbstractJpaPersistable
import com.leftindust.mockingbird.extensions.toLong
import com.leftindust.mockingbird.graphql.types.input.GraphQLClinicInput
import org.hibernate.Session
import javax.persistence.Entity
import javax.persistence.JoinColumn
import javax.persistence.OneToMany
import javax.persistence.OneToOne

@Entity(name = "clinic")
class Clinic(
    val name: String,
    @OneToOne
    @JoinColumn(nullable = false)
    val address: Address,
    @OneToMany
    val doctors: Set<Doctor>,
) : AbstractJpaPersistable<Long>() {
    constructor(gqlClinicInput: GraphQLClinicInput, session: Session) : this(
        name = gqlClinicInput.name,
        address = Address(gqlClinicInput.address),
        doctors = gqlClinicInput.doctors?.map { session.get(Doctor::class.java, it.toLong()) }?.toSet() ?: emptySet(),
    )
}