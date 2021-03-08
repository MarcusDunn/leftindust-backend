package com.leftindust.mediq.dao.entity

import com.leftindust.mediq.dao.entity.superclasses.AbstractJpaPersistable
import javax.persistence.*

@Entity(name = "mediq_user")
class MediqUser(
    @Column(name = "unique_id", unique = true, nullable = false)
    val uniqueId: String,
    @OneToOne(cascade = [(CascadeType.ALL)], orphanRemoval = false, fetch = FetchType.LAZY)
    var group: MediqGroup? = null,
    @Embedded
    var settings: UserSettings,
) : AbstractJpaPersistable<Long>()