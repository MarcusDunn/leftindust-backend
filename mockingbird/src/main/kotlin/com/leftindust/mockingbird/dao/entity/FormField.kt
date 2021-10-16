package com.leftindust.mockingbird.dao.entity

import com.leftindust.mockingbird.dao.entity.superclasses.AbstractJpaPersistable
import java.sql.Date
import javax.persistence.*

/**
 * describes a single field in a form. A field has a [dataType] that determines optional restrictions on the data
 */
@Entity
class FormField private constructor(
    val title: String,
    val number: Int,
    @Enumerated(value = EnumType.STRING)
    val dataType: DataType,
    @ElementCollection(fetch = FetchType.EAGER)
    val multiSelectPossibilities: List<String>? = null,
    @Column(nullable = true)
    val intUpperBound: Int? = null,
    @Column(nullable = true)
    val intLowerBound: Int? = null,
    @Column(nullable = true)
    val floatUpperBound: Float? = null,
    @Column(nullable = true)
    val floatLowerBound: Float? = null,
    @Column(nullable = true)
    val dateUpperBound: Date? = null,
    @Column(nullable = true)
    val dateLowerBound: Date? = null,
    @Column(nullable = true)
    val textRegex: String? = null,
    @Column(nullable = true, length = 5_000)
    val jsonMetaData: String? = null,
) : AbstractJpaPersistable() {
    constructor(
        title: String,
        number: Int,
        dataType: DataType,
        intUpperBound: Int?,
        intLowerBound: Int?,
        jsonMetaData: String? = null,
    ) : this(
        title = title,
        number = number,
        dataType = dataType,
        intUpperBound = intUpperBound,
        intLowerBound = intLowerBound,
        textRegex = null,
        jsonMetaData = jsonMetaData
    ) {
        if (dataType != DataType.Integer) {
            throw IllegalArgumentException("illegal arguments for formFeild of type $dataType")
        }
    }

    constructor(
        title: String,
        number: Int,
        dataType: DataType,
        textRegex: String?,
        jsonMetaData: String? = null
    ) : this(
        title = title,
        number = number,
        dataType = dataType,
        textRegex = textRegex,
        jsonMetaData = jsonMetaData,
        dateUpperBound = null,
    ) {
        if (dataType != DataType.Text) {
            throw IllegalArgumentException("illegal arguments for formFeild of type $dataType")
        }
    }

    constructor(
        title: String,
        number: Int,
        dataType: DataType,
        dateUpperBound: Date?,
        dateLowerBound: Date?,
        jsonMetaData: String? = null,
    ) : this(
        title = title,
        number = number,
        dataType = dataType,
        dateUpperBound = dateUpperBound,
        dateLowerBound = dateLowerBound,
        jsonMetaData = jsonMetaData,
        textRegex = null,
    ) {
        if (dataType != DataType.Date) {
            throw IllegalArgumentException("illegal arguments for formFeild of type $dataType")
        }
    }

    constructor(
        title: String,
        number: Int,
        dataType: DataType,
        multiSelectPossibilities: List<String>?,
        jsonMetaData: String? = null,
    ) : this(
        title = title,
        number = number,
        dataType = dataType,
        multiSelectPossibilities = multiSelectPossibilities,
        jsonMetaData = jsonMetaData,
        textRegex = null,
    ) {
        if (dataType != DataType.MultiMuliSelect && dataType != DataType.SingleMuliSelect) {
            throw IllegalArgumentException("illegal arguments for formFeild of type $dataType")
        }
    }

    constructor(
        title: String,
        number: Int,
        dataType: DataType,
        floatUpperBound: Float?,
        floatLowerBound: Float?,
        jsonMetaData: String? = null,
    ) : this(
        title = title,
        number = number,
        dataType = dataType,
        floatLowerBound = floatLowerBound,
        floatUpperBound = floatUpperBound,
        jsonMetaData = jsonMetaData,
        dateUpperBound = null,
    ) {
        if (dataType != DataType.MultiMuliSelect) {
            throw IllegalArgumentException("illegal arguments for formFeild of type $dataType")
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


