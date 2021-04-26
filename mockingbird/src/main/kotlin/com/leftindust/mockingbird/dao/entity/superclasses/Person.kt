package com.leftindust.mockingbird.dao.entity.superclasses

import com.leftindust.mockingbird.dao.entity.*
import java.sql.Timestamp
import javax.persistence.*

@MappedSuperclass
abstract class Person(
    @OneToOne(fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    @JoinColumn(name = "name_info_id")
    var nameInfo: NameInfo,
    @Column(name = "date_of_birth", nullable = false)
    var dateOfBirth: Timestamp,
    @OneToMany(fetch = FetchType.EAGER, cascade = [CascadeType.ALL], orphanRemoval = true)
    var address: Set<Address> = emptySet(),
    @OneToMany(fetch = FetchType.EAGER, cascade = [CascadeType.ALL], orphanRemoval = true)
    var email: Set<Email> = emptySet(),
    @OneToMany(fetch = FetchType.EAGER, cascade = [CascadeType.ALL], orphanRemoval = true)
    var phone: Set<Phone> = emptySet(),
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
        return "Person(nameInfo=$nameInfo, dateOfBirth=$dateOfBirth, address=$address, email=$email, phone=$phone, user=$user, schedule=$schedule)"
    }
}