package com.leftindust.mockingbird.dao.entity

import com.leftindust.mockingbird.dao.entity.superclasses.AbstractJpaPersistable
import com.leftindust.mockingbird.graphql.types.input.GraphQLGroupInput
import javax.persistence.Entity

@Entity(name = "mediq_group")
class MediqGroup(
    val name: String,
) : AbstractJpaPersistable<Long>() {
    constructor(graphQLGroupInput: GraphQLGroupInput) : this(name = graphQLGroupInput.name)
}