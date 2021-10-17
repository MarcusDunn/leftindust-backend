package com.leftindust.mockingbird.dao.entity

import com.leftindust.mockingbird.dao.entity.superclasses.AbstractJpaPersistable
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.OneToMany

/**
 * describes the format of a form. it DOES NOT hold data. It's instead a template for a record form.
 */
@Entity
class Form(
    var name: String,
    @OneToMany(
        fetch = FetchType.EAGER,
        targetEntity = FormSection::class,
        orphanRemoval = true,
        cascade = [CascadeType.ALL]
    )
    val sections: Set<FormSection>,
) : AbstractJpaPersistable()
