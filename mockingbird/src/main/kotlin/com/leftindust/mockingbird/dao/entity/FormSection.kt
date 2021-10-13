package com.leftindust.mockingbird.dao.entity

import com.leftindust.mockingbird.dao.entity.superclasses.AbstractJpaPersistable
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.OneToMany

@Entity(name = "form_sections")
class FormSection private constructor(
    @OneToMany(fetch = FetchType.EAGER)
    val fields: MutableSet<FormField>,
    val name: String,
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
}
