package com.leftindust.caper.sql.tables

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object Groups : IntIdTable() {
    val name = varchar("name", 255)
}

class Group(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Group>(Groups)

    var name by Groups.name
}