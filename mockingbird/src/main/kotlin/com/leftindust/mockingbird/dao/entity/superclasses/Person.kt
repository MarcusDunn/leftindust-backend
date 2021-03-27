package com.leftindust.mockingbird.dao.entity.superclasses

import com.leftindust.mockingbird.dao.entity.Address
import com.leftindust.mockingbird.dao.entity.Email
import com.leftindust.mockingbird.dao.entity.Phone
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
    @OneToMany(fetch = FetchType.EAGER)
    var addresses: Set<Address> = emptySet(),
    @OneToMany(fetch = FetchType.EAGER)
    var emails: Set<Email> = emptySet(),
    @OneToMany(fetch = FetchType.EAGER)
    var phones: Set<Phone> = emptySet(),
) : AbstractJpaPersistable<Long>() {
    override fun toString(): String {
        return "Person(firstName='$firstName', lastName='$lastName', middleName=$middleName, dateOfBirth=$dateOfBirth, addresses=$addresses, emails=$emails, phones=$phones)"
    }
}