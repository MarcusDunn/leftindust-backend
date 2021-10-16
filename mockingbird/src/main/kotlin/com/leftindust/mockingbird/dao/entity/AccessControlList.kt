package com.leftindust.mockingbird.dao.entity

import com.leftindust.mockingbird.dao.entity.superclasses.AbstractJpaPersistable
import javax.persistence.*

@Entity
class AccessControlList(
    @ManyToOne(cascade = [(CascadeType.ALL)], fetch = FetchType.EAGER)
    val group: MediqGroup? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    val mediqUser: MediqUser? = null,
    @ManyToOne(cascade = [(CascadeType.ALL)], fetch = FetchType.EAGER)
    val action: Action,
) : AbstractJpaPersistable()