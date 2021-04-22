package com.leftindust.mockingbird.dao.entity

import com.leftindust.mockingbird.dao.entity.superclasses.AbstractJpaPersistable
import com.leftindust.mockingbird.extensions.getOrDefault
import com.leftindust.mockingbird.graphql.types.input.GraphQLUserEditInput
import com.leftindust.mockingbird.graphql.types.input.GraphQLUserInput
import javax.persistence.*

@Entity(name = "mediq_user")
class MediqUser(
    @Column(name = "unique_id", unique = true, nullable = false)
    val uniqueId: String,
    @OneToOne(cascade = [(CascadeType.ALL)], orphanRemoval = false, fetch = FetchType.LAZY)
    var group: MediqGroup? = null,
) : AbstractJpaPersistable<Long>() {
    constructor(graphQLUserInput: GraphQLUserInput, group: MediqGroup?) : this(
        uniqueId = graphQLUserInput.uid,
        group = group,
    )
}