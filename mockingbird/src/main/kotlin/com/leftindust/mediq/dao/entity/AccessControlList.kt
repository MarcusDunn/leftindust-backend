package com.leftindust.mediq.dao.entity

import com.leftindust.mediq.dao.entity.superclasses.AbstractJpaPersistable
import javax.persistence.*

@Entity(name = "access_control_list")
class AccessControlList(
    @OneToOne(cascade = [(CascadeType.ALL)], orphanRemoval = false, fetch = FetchType.LAZY)
    val group: MediqGroup?,
    @OneToOne(orphanRemoval = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "mediq_user")
    val mediqUser: MediqUser?,
    @OneToOne(cascade = [(CascadeType.ALL)], orphanRemoval = false, fetch = FetchType.LAZY)
    val action: Action,
) : AbstractJpaPersistable<Long>()