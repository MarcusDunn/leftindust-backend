package com.leftindust.mockingbird.dao.entity

import com.google.gson.JsonObject
import com.leftindust.mockingbird.dao.entity.enums.RecordType
import com.leftindust.mockingbird.dao.entity.superclasses.AbstractJpaPersistable
import java.sql.Timestamp
import javax.persistence.*

@Entity(name = "mediq_record")
class MediqRecord(
    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var patient: Patient,
    @Column(name = "creation_date")
    val creationDate: Timestamp,
    @Enumerated(EnumType.STRING)
    val type: RecordType,
    @Column(name = "json_blob", length = 10_000)
    @Basic(fetch = FetchType.LAZY)
    val jsonBlob: String? = JsonObject().toString()
) : AbstractJpaPersistable<Long>()
