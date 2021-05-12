package com.leftindust.mockingbird.dao.entity

import com.leftindust.mockingbird.dao.entity.superclasses.AbstractJpaPersistable
import javax.persistence.*

@Entity(name = "access_control_list")
class AccessControlList(
    @OneToOne(cascade = [(CascadeType.ALL)], orphanRemoval = false, fetch = FetchType.EAGER)
    val group: MediqGroup? = null,
    @OneToOne(orphanRemoval = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "mediq_user")
    val mediqUser: MediqUser? = null,
    @OneToOne(cascade = [(CascadeType.ALL)], orphanRemoval = false, fetch = FetchType.EAGER)
    val action: Action,
) : AbstractJpaPersistable<Long>()