package com.leftindust.mockingbird.dao.entity

import biweekly.property.RecurrenceRule
import com.leftindust.mockingbird.dao.entity.converters.RecurrenceConverter
import com.leftindust.mockingbird.dao.entity.enums.ConsumptionMethod
import com.leftindust.mockingbird.dao.entity.superclasses.AbstractJpaPersistable
import java.sql.Timestamp
import javax.persistence.*

@Entity
class Prescription(
    @Column(name = "filing_date", nullable = false)
    val filingDate: Timestamp,
    @Column(name = "medication_name", nullable = false)
    val medicationName: String,
    @Embedded
    @AttributeOverrides(
        AttributeOverride(name = "magnitude", column = Column(name = "medication_dosage_magnitude")),
        AttributeOverride(name = "unit", column = Column(name = "medication_dosage_unit")),
    )
    val medicationDosage: Measurement,
    @Embedded
    @AttributeOverrides(
        AttributeOverride(name = "magnitude", column = Column(name = "amount_magnitude")),
        AttributeOverride(name = "unit", column = Column(name = "amount_unit")),
    )
    val amount: Measurement,
    @Column(name = "frequency_recurrence_rule", nullable = true)
    @Convert(converter = RecurrenceConverter::class)
    val frequency: RecurrenceRule,
    @Enumerated(EnumType.STRING)
    @Column(name = "consumption_method", nullable = false)
    val consumptionMethod: ConsumptionMethod,
    @Column(name = "generic_permitted", nullable = false)
    val genericPermitted: Boolean,
    @Column(name = "number_of_refills", nullable = false)
    val numberOfRefills: Int,
    @ElementCollection
    @CollectionTable(name = "comments", joinColumns = [JoinColumn(name = "prescription_id")])
    @Column(name = "comment", nullable = false)
    val comments: List<String> = emptyList(),
    @ManyToOne()
    val doctor: Doctor,
) : AbstractJpaPersistable<Long>()