package com.leftindust.mockingbird.dao.entity

import com.leftindust.mockingbird.dao.entity.superclasses.AbstractJpaPersistable
import com.leftindust.mockingbird.graphql.types.input.GraphQLVisitInput
import javax.persistence.*

@Entity
class Visit(
    @OneToOne(cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    var event: Event,
    var title: String? = null,
    var description: String? = null,
    @ElementCollection
    // stored as URLS to the code
    var icds: Set<String>,
) : AbstractJpaPersistable() {

    constructor(visitInput: GraphQLVisitInput, event: Event) : this(
        event = event,
        title = visitInput.title,
        description = visitInput.description,
        icds = visitInput.foundationIcdCodes.map { it.url }.toSet(),
    )
}