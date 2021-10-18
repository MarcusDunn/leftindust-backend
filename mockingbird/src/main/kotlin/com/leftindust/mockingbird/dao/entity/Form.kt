package com.leftindust.mockingbird.dao.entity

import com.leftindust.mockingbird.dao.entity.superclasses.AbstractJpaPersistable
import com.leftindust.mockingbird.graphql.types.input.GraphQLFormTemplateInput
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.OneToMany

/**
 * describes the format of a form. it DOES NOT hold data. It's instead a template for a record form.
 */
@Entity
class Form private constructor(
    var name: String,
    @OneToMany(
        fetch = FetchType.EAGER,
        orphanRemoval = true,
        cascade = [CascadeType.ALL]
    )
    val sections: MutableSet<FormSection>,
) : AbstractJpaPersistable() {
    constructor(sections: Set<FormSection>, name: String) : this(name = name, sections = sections.toMutableSet())
    constructor(form: GraphQLFormTemplateInput) : this(
        name = form.name,
        sections = form.sections.map { FormSection(it) }.toSet()
    )
}
