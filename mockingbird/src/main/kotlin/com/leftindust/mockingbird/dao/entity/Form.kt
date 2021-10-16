package com.leftindust.mockingbird.dao.entity

import com.leftindust.mockingbird.dao.entity.superclasses.AbstractJpaPersistable
import javax.persistence.*

/**
 * describes the format of a form. it DOES NOT hold data. It's instead a template for a record form.
 */
@Entity
class Form(
    val name: String,
    @OneToMany(fetch = FetchType.EAGER, targetEntity = FormSection::class)
    val sections: Set<FormSection>,
) : AbstractJpaPersistable()
