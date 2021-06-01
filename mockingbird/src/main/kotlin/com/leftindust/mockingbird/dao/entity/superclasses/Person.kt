package com.leftindust.mockingbird.dao.entity.superclasses

import com.leftindust.mockingbird.dao.entity.*
import org.hibernate.annotations.JoinFormula
import java.sql.Timestamp
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
    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    var events: MutableSet<Event> = emptySet<Event>().toMutableSet()
) : AbstractJpaPersistable() {
    init {
        // if user exists, set the user nameInfo to the info stored on the person instead to prevent inconsistencies
        user?.let { it.nameInfo = nameInfo }
    }

    fun getEventsBetween(from: Timestamp, to: Timestamp): List<Event> {
        return this.events.filter { isBetween(from, it, to) }
    }

    private fun isBetween(
        from: Timestamp,
        event: Event,
        to: Timestamp
    ): Boolean {
        return from.before(event.startTime) && to.after(event.startTime)
    }

    fun addEvent(eventEntity: Event) {
        events.add(eventEntity)
    }
}