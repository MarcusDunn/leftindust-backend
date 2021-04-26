package com.leftindust.mockingbird.dao.entity

import com.leftindust.mockingbird.dao.entity.superclasses.AbstractJpaPersistable
import com.leftindust.mockingbird.graphql.types.input.GraphQLUserInput
import javax.persistence.*

@Entity(name = "mediq_user")
class MediqUser(
    @Column(name = "unique_id", unique = true, nullable = false)
    val uniqueId: String,
    @OneToOne(cascade = [(CascadeType.ALL)], orphanRemoval = false, fetch = FetchType.LAZY)
    var group: MediqGroup? = null,
    @OneToOne(fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    @JoinColumn(name = "name_info_id")
    var nameInfo: NameInfo,
) : AbstractJpaPersistable<Long>() {
    constructor(graphQLUserInput: GraphQLUserInput, group: MediqGroup?) : this(
        nameInfo = NameInfo(graphQLUserInput.nameInfo),
        uniqueId = graphQLUserInput.uid,
        group = group,
    )
}