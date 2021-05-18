package com.leftindust.mockingbird.dao.entity.superclasses

import com.leftindust.mockingbird.dao.entity.*
import java.sql.Date
import javax.persistence.*

@MappedSuperclass
abstract class Person(
    @OneToOne(fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    @JoinColumn(name = "name_info_id", nullable = false)
    var nameInfo: NameInfo,
    @OneToMany(fetch = FetchType.EAGER, cascade = [CascadeType.ALL], orphanRemoval = true)
    var address: MutableSet<Address> = mutableSetOf(),
    @OneToMany(fetch = FetchType.EAGER, cascade = [CascadeType.ALL], orphanRemoval = true)
    var email: MutableSet<Email> = mutableSetOf(),
    @OneToMany(fetch = FetchType.EAGER, cascade = [CascadeType.ALL], orphanRemoval = true)
    var phone: MutableSet<Phone> = mutableSetOf(),
    @OneToOne
    var user: MediqUser? = null,
    @Embedded
    var schedule: Schedule = Schedule(),
) : AbstractJpaPersistable<Long>() {
    init {
        // if user exists, set the user nameInfo to the info stored on the person instead to prevent inconsistencies
        user?.let { it.nameInfo = nameInfo }
    }

    override fun toString(): String {
        return "Person(nameInfo=$nameInfo, address=$address, email=$email, phone=$phone, user=$user, schedule=$schedule)"
    }
}