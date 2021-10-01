package com.leftindust.mockingbird.dao.entity

import com.leftindust.mockingbird.dao.entity.superclasses.AbstractJpaPersistable
import java.sql.Date
import java.util.regex.Pattern
import javax.persistence.*

/**
 * describes a single field in a form. A field has a [dataType] that determines optional restrictions on the data
 */
@Entity(name = "form_field")
class FormField(
    @ManyToOne(targetEntity = FormSection::class, optional = false)
    @JoinColumn(name = "form_section_id", nullable = false)
    val form: FormSection,
    @Column(name = "data_type")
    @Enumerated(value = EnumType.STRING)
    val dataType: DataType,
    @ElementCollection(fetch = FetchType.EAGER)
    val multiSelectPossibilities: List<String>?,
    @Column(name = "int_upper_bound", nullable = true)
    val intUpperBound: Int?,
    @Column(name = "int_lower_bound", nullable = true)
    val intLowerBound: Int?,
    @Column(name = "float_upper_bound", nullable = true)
    val floatUpperBound: Int?,
    @Column(name = "float_lower_bound", nullable = true)
    val floatLowerBound: Int?,
    @Column(name = "date_upper_bound", nullable = true)
    val dateUpperBound: Date?,
    @Column(name = "date_lower_bound", nullable = true)
    val dateLowerBound: Date?,
    @Column(name = "text_regex", nullable = true)
    val textRegex: String?,
    @Column(name = "file_data_type", nullable = true)
    val fileDataType: FileDataType?,
    @Column(name = "json_metadata", nullable = true, length = 5_000)
    val jsonMetaData: String?,
) : AbstractJpaPersistable() {
    init {
        val isValid = when (dataType) {
            DataType.MultiMuliSelect -> multiSelectPossibilities != null
            DataType.SingleMuliSelect -> multiSelectPossibilities != null
            DataType.Text -> {
                runCatching { Pattern.compile(textRegex ?: ".*") }.isSuccess
            }
            DataType.Integer -> (intLowerBound ?: Int.MIN_VALUE) < (intUpperBound ?: Int.MAX_VALUE)
            DataType.Float -> (floatLowerBound ?: Int.MIN_VALUE) < (floatUpperBound ?: Int.MAX_VALUE)
            DataType.Date -> (dateUpperBound?.let { dateLowerBound?.before(it) } != false)
            DataType.File -> fileDataType != null
        }
        if (!isValid) {
            throw Exception("invalid FormField")
        }
    }

    enum class DataType {
        SingleMuliSelect,
        MultiMuliSelect,
        File,
        Text,
        Integer,
        Date,
        Float,
    }

    enum class FileDataType {
        Image,
        Pdf,
    }
}


