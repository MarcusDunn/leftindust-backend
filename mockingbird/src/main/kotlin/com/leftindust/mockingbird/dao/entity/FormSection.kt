package com.leftindust.mockingbird.dao.entity

import com.leftindust.mockingbird.dao.entity.superclasses.AbstractJpaPersistable
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.OneToMany

@Entity(name = "form_sections")
class FormSection(
    @Column(name = "section_number")
    val sectionNumber: Int,
    @OneToMany
    val fields: Set<FormField>,
): AbstractJpaPersistable() {

}
