package com.leftindust.mockingbird.dao.entity

import com.leftindust.mockingbird.dao.entity.superclasses.AbstractJpaPersistable
import com.leftindust.mockingbird.graphql.types.input.GraphQLFormSectionInput
import javax.persistence.*

@Entity
class FormSection private constructor(
    @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true, cascade = [CascadeType.ALL])
    val fields: MutableSet<FormField>,
    @Column(length = 50_000)
    val description: String?,
    var name: String,
    val number: Int,
) : AbstractJpaPersistable() {
    constructor(
        name: String,
        number: Int,
        description: String?,
        fields: Set<FormField>,
    ) : this(
        name = name,
        number = number,
        description = description,
        fields = fields.toMutableSet(),
    )

    constructor(graphQLFormSectionInput: GraphQLFormSectionInput) : this(
        name = graphQLFormSectionInput.name,
        number = graphQLFormSectionInput.number,
        description = graphQLFormSectionInput.description,
        fields = graphQLFormSectionInput.fields.map { FormField(it) }.toSet()
    )
}
