package com.leftindust.mockingbird.dao.entity

import com.leftindust.mockingbird.dao.entity.superclasses.AbstractJpaPersistable
import com.leftindust.mockingbird.graphql.types.input.GraphQLNameInput
import javax.persistence.Column
import javax.persistence.Entity

@Entity(name = "name_info")
class NameInfo(
    @Column(name = "first_name", nullable = false)
    var firstName: String,
    @Column(name = "last_name", nullable = false)
    var lastName: String,
    @Column(name = "middle_name", nullable = true)
    var middleName: String?,
) : AbstractJpaPersistable<Long>() {
    constructor(graphQLNameInput: GraphQLNameInput) : this(
        firstName = graphQLNameInput.firstName,
        lastName = graphQLNameInput.lastName,
        middleName = graphQLNameInput.middleName,
    )

    override fun toString(): String {
        return "NameInfo(firstName='$firstName', lastName='$lastName', middleName=$middleName)"
    }
}