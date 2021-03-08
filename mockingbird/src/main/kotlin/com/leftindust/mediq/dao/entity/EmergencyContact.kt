package com.leftindust.mediq.dao.entity

import com.leftindust.mediq.dao.entity.enums.Relationship
import com.leftindust.mediq.dao.entity.superclasses.AbstractJpaPersistable
import javax.persistence.*

@Entity(name = "emergency_contact")
class EmergencyContact(
    @Column(name = "contact_id", nullable = false, unique = true)
    val cid: Long,
    @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.MERGE])
    @JoinColumn(name = "patient_id", referencedColumnName = "id")
    var patient: Patient,
    @Enumerated(EnumType.STRING)
    var relationship: Relationship,
    @Column(name = "first_name", nullable = false)
    var firstName: String,
    @Column(name = "middle_name", nullable = true)
    var middleName: String? = null,
    @Column(name = "last_name", nullable = false)
    var lastName: String,
    @Column(name = "cell_number", nullable = true)
    var cellNumber: Long? = null,
    @Column(name = "home_number", nullable = true)
    var homeNumber: Long? = null,
    @Column(name = "work_number", nullable = true)
    var workNumber: Long? = null,
) : AbstractJpaPersistable<Long>()