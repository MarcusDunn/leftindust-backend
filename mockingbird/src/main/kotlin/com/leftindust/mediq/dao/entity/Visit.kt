package com.leftindust.mediq.dao.entity

import com.leftindust.mediq.dao.entity.converters.IcdCodeConverter
import com.leftindust.mediq.dao.entity.superclasses.AbstractJpaPersistable
import com.leftindust.mediq.graphql.types.GraphQLVisitInput
import com.leftindust.mediq.graphql.types.icd.FoundationIcdCode
import java.sql.Timestamp
import javax.persistence.*

@Entity
class Visit(
    @Column(name = "time_booked")
    var timeBooked: Timestamp,
    @Column(name = "time_of_visit")
    var timeOfVisit: Timestamp,

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
    constructor(visitInput: GraphQLVisitInput, patient: Patient, doctor: Doctor) : this(
        timeBooked = visitInput.timeBooked.toTimestamp(),
        timeOfVisit = visitInput.timeOfVisit.toTimestamp(),
        title = visitInput.title,
        description = visitInput.description,
        patient = patient,
        doctor = doctor,
        icdFoundationCode = visitInput.foundationIcdCode,
    )
}