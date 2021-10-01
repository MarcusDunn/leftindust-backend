package com.leftindust.mockingbird.dao.entity

import com.leftindust.mockingbird.dao.entity.superclasses.AbstractJpaPersistable
import java.sql.Date
import java.util.regex.Pattern
import javax.persistence.*

/**
 * describes the format of a form. it DOES NOT hold data. It's instead a template for a record form.
 */
@Entity(name = "form")
class Form(
    @OneToMany(fetch = FetchType.EAGER)
    val fields: Set<FormField>,
) : AbstractJpaPersistable()

/**
 * describes a single field in a form. A field has a [dataType] that determines optional restrictions on the data
 */
@Entity(name = "form_field")
class FormField(
    @ManyToOne(targetEntity = Form::class, optional = false)
    @JoinColumn(name = "form_id", nullable = false)
    val form: Form,
    @Column(name = "data_type")
    @Enumerated(value = EnumType.STRING)
    val dataType: DataType,
    @ElementCollection(fetch = FetchType.EAGER)
    val multiSelectPossibilities: List<String>?,
    @Column(name = "number_upper_bound", nullable = true)
    val intUpperBound: Int?,
    @Column(name = "number_lower_bound", nullable = true)
    val intLowerBound: Int?,
    @Column(name = "date_upper_bound", nullable = true)
    val dateUpperBound: Date?,
    @Column(name = "date_lower_bound", nullable = true)
    val dateLowerBound: Date?,
    @Column(name = "text_regex", nullable = true)
    val textRegex: String?,
    @Column(name = "json_metadata", nullable = true, length = 5_000)
    val jsonMetaData: String,
) : AbstractJpaPersistable() {
    /**
     * asserts the following:
     * if the datatype is MuliSelect, multiSelectPossibilities cannot be null
     * if the datatype is text and a regex is provided, it must be valid regex
     * if the datatype is number and both and upper and lower bound are provided, the lower bound must be less than the upper
     * if the datatype is a date and both and upper and lower bound are provided, the lower bound must be before the upper
     */
    init {
        val isValid = when (dataType) {
            DataType.MultiMuliSelect -> multiSelectPossibilities != null
            DataType.SingleMuliSelect -> multiSelectPossibilities != null
            DataType.Text -> {
                runCatching { Pattern.compile(textRegex ?: ".*") }.isSuccess
            }
            DataType.Integer -> (intLowerBound ?: Int.MIN_VALUE) < (intUpperBound ?: Int.MAX_VALUE)
            DataType.Float -> TODO()
            DataType.Date -> (dateUpperBound?.let { dateLowerBound?.before(it) } != false)
        }
        if (!isValid) {
            throw Exception("invalid FormField")
        }
    }

    enum class DataType {
        SingleMuliSelect,
        MultiMuliSelect,
        Text,
        Integer,
        Date,
        Float,
    }
}

