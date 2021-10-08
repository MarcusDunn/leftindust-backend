package com.leftindust.mockingbird.dao.entity

import com.leftindust.mockingbird.dao.entity.superclasses.AbstractJpaPersistable
import java.sql.Date
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.OneToMany

@Entity(name = "form_sections")
class FormSection(
    val name: String,
    val number: Int,
    @OneToMany(fetch = FetchType.EAGER)
    val fields: MutableSet<FormField>,
) : AbstractJpaPersistable() {
    constructor(name: String, formFieldCreators: Set<FormFieldCreator>, number: Int) : this(
        name = name,
        number = number,
        fields = mutableSetOf<FormField>(),
    ) {
        this.fields.addAll(formFieldCreators.map { it.mkFormField(this) })
    }

    /**
     * works as a partial construction of a FormField, mkFormField allows injecting FormSection after the FormSection
     * has been created
     */
    data class FormFieldCreator(
        val number: Int,
        val title: String,
        val dataType: FormField.DataType,
        val multiSelectPossibilities: List<String>? = null,
        val intUpperBound: Int? = null,
        val intLowerBound: Int? = null,
        val floatUpperBound: Int? = null,
        val floatLowerBound: Int? = null,
        val dateUpperBound: Date? = null,
        val dateLowerBound: Date? = null,
        val textRegex: String? = null,
        val jsonMetaData: String? = null,
    ) {
        fun mkFormField(formSection: FormSection): FormField {
            return FormField(
                title = title,
                number = number,
                form = formSection,
                dataType = this.dataType,
                multiSelectPossibilities = this.multiSelectPossibilities,
                intUpperBound = this.intUpperBound,
                intLowerBound = this.intLowerBound,
                floatUpperBound = this.floatUpperBound,
                floatLowerBound = this.floatLowerBound,
                dateUpperBound = this.dateUpperBound,
                dateLowerBound = this.dateLowerBound,
                textRegex = this.textRegex,
                jsonMetaData = this.jsonMetaData,
            )
        }
    }
}
