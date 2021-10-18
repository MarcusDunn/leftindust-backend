package com.leftindust.mockingbird.dao.entity

import com.leftindust.mockingbird.dao.entity.superclasses.AbstractJpaPersistable
import com.leftindust.mockingbird.graphql.types.input.GraphQLFormSectionInput
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.OneToMany

@Entity
class FormSection private constructor(
    @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true, cascade = [CascadeType.ALL])
    val fields: MutableSet<FormField>,
    var name: String,
    val number: Int,
) : AbstractJpaPersistable() {
    constructor(
        name: String,
        number: Int,
        fields: Set<FormField>,
    ) : this(
        name = name,
        number = number,
        fields = fields.toMutableSet(),
    )

    constructor(graphQLFormSectionInput: GraphQLFormSectionInput) : this(
        name = graphQLFormSectionInput.name,
        number = graphQLFormSectionInput.number,
        fields = graphQLFormSectionInput.fields.map { FormField(it) }.toSet()
    )
}
