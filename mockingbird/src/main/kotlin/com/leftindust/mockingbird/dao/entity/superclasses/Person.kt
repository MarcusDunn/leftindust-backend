package com.leftindust.mockingbird.dao.entity.superclasses

import com.leftindust.mockingbird.dao.entity.Email
import java.sql.Timestamp
import javax.persistence.Column
import javax.persistence.MappedSuperclass
import javax.persistence.OneToMany

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
    @Column(name = "address", nullable = true)
    var address: String?,
    @OneToMany
    var emails: Set<Email> = emptySet(),
    @Column(name = "cell_phone", nullable = true)
    var cellPhone: String?,
    @Column(name = "work_phone", nullable = true)
    var workPhone: String?,
    @Column(name = "home_phone", nullable = true)
    var homePhone: String?,
) : AbstractJpaPersistable<Long>() {
    override fun toString(): String {
        return "Person(firstName='$firstName', lastName='$lastName', middleName=$middleName, dateOfBirth=$dateOfBirth, address=$address, emails=$emails, cellPhone=$cellPhone, workPhone=$workPhone, homePhone=$homePhone)"
    }
}