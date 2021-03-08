package com.leftindust.mediq.dao.entity

import com.google.gson.JsonObject
import com.leftindust.mediq.dao.entity.enums.RecordType
import com.leftindust.mediq.dao.entity.superclasses.AbstractJpaPersistable
import java.sql.Timestamp
import javax.persistence.*

@Entity(name = "mediq_record")
class MediqRecord(
    @Column(name = "record_id")
    val rid: Int,
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
