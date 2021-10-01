package com.leftindust.mockingbird.dao.entity

import com.leftindust.mockingbird.dao.entity.superclasses.AbstractJpaPersistable
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.OneToMany

/**
 * describes the format of a form. it DOES NOT hold data. It's instead a template for a record form.
 */
@Entity(name = "form")
class Form(
    @OneToMany(fetch = FetchType.EAGER)
    val fields: Set<FormSection>,
) : AbstractJpaPersistable()

