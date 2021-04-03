package com.leftindust.mockingbird.dao.entity

import com.leftindust.mockingbird.dao.entity.converters.IcdCodeConverter
import com.leftindust.mockingbird.dao.entity.superclasses.AbstractJpaPersistable
import com.leftindust.mockingbird.graphql.types.GraphQLVisitInput
import com.leftindust.mockingbird.graphql.types.icd.FoundationIcdCode
import javax.persistence.*

@Entity
class Visit(
    @OneToOne
    var event: Event,
    var title: String? = null,

    var description: String? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    var patient: Patient,
    @ManyToOne(fetch = FetchType.LAZY)
    var doctor: Doctor,

    @Column(name = "icd_foundation_code", nullable = false)
    @Convert(converter = IcdCodeConverter::class)
    var icdFoundationCode: FoundationIcdCode,
) : AbstractJpaPersistable<Long>() {
    constructor(visitInput: GraphQLVisitInput, patient: Patient, doctor: Doctor, event: Event) : this(
        event = event,
        title = visitInput.title,
        description = visitInput.description,
        patient = patient,
        doctor = doctor,
        icdFoundationCode = visitInput.foundationIcdCode,
    )
}