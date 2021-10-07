package com.leftindust.mockingbird.dao.entity

import com.google.gson.JsonObject
import com.leftindust.mockingbird.dao.entity.enums.RecordType
import com.leftindust.mockingbird.dao.entity.superclasses.AbstractJpaPersistable
import com.leftindust.mockingbird.graphql.types.input.GraphQLRecordInput
import java.sql.Timestamp
import java.time.Instant
import javax.persistence.*

@Entity(name = "mediq_record")
class MediqRecord(
    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var patient: Patient,
    @Column(name = "creation_date", nullable = false)
    val creationDate: Timestamp,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val type: RecordType,
    @Column(name = "json_blob", length = 10_000, nullable = false)
    @Basic(fetch = FetchType.LAZY)
    val jsonBlob: String,
) : AbstractJpaPersistable() {
    constructor(record: GraphQLRecordInput, patient: Patient) : this(
        patient = patient,
        creationDate = Timestamp.from(Instant.now()),
        type = record.recordType,
        jsonBlob = record.jsonBlob,
    )
}
