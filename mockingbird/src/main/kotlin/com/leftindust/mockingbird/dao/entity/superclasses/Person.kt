package com.leftindust.mockingbird.dao.entity.superclasses

import com.leftindust.mockingbird.dao.entity.*
import java.sql.Timestamp
import javax.persistence.*

@MappedSuperclass
abstract class Person(
    @Column(name = "first_name", nullable = false)
    var firstName: String,
    @Column(name = "last_name", nullable = false)
    var lastName: String,
    @Column(name = "middle_name", nullable = true)
    var middleName: String?,
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
    override fun toString(): String {
        return "Person(firstName='$firstName', lastName='$lastName', middleName=$middleName, dateOfBirth=$dateOfBirth, address=$address, email=$email, phone=$phone, user=$user, schedule=$schedule)"
    }
}