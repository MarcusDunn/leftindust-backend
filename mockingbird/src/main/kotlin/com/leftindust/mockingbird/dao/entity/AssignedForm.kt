package com.leftindust.mockingbird.dao.entity

import com.leftindust.mockingbird.dao.entity.superclasses.AbstractJpaPersistable
import javax.persistence.Entity
import javax.persistence.OneToOne

@Entity
class AssignedForm(
    @OneToOne
    val formTemplate: Form,
    @OneToOne
    val patient: Patient,
) : AbstractJpaPersistable() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as AssignedForm

        if (formTemplate != other.formTemplate) return false
        if (patient != other.patient) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + formTemplate.hashCode()
        result = 31 * result + patient.hashCode()
        return result
    }
}