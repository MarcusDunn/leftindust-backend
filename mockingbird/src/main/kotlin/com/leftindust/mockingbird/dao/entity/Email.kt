package com.leftindust.mockingbird.dao.entity

import com.leftindust.mockingbird.dao.entity.superclasses.AbstractJpaPersistable
import com.leftindust.mockingbird.graphql.types.GraphQLEmail
import com.leftindust.mockingbird.graphql.types.GraphQLEmailType
import javax.persistence.Column
import javax.persistence.Entity


@Entity(name = "email")
class Email(
    @Column(name = "type", nullable = false)
    var type: GraphQLEmailType,
    @Column(name = "email", nullable = false)
    var email: String,
) : AbstractJpaPersistable<Long>() {
    constructor(graphQLEmail: GraphQLEmail) : this(graphQLEmail.type, graphQLEmail.email)
}