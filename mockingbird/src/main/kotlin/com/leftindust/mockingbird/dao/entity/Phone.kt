package com.leftindust.mockingbird.dao.entity

import com.leftindust.mockingbird.dao.entity.superclasses.AbstractJpaPersistable
import com.leftindust.mockingbird.graphql.types.GraphQLPhone
import com.leftindust.mockingbird.graphql.types.GraphQLPhoneType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated

@Entity
class Phone(
    @Column(name = "number", nullable = false)
    var number: Long,
    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    var type: GraphQLPhoneType
) : AbstractJpaPersistable<Long>() {
    constructor(graphQLPhone: GraphQLPhone) : this(graphQLPhone.number, graphQLPhone.type)
}