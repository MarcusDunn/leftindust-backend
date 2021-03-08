package com.leftindust.caper.sql.tables

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object Actions : IntIdTable() {
    val columnName = varchar("column_name", 255).nullable()
    val permissionType = varchar("permission_type", 255).nullable()
    val target = varchar("target", 255)
    val rowId = integer("row_id").nullable()
}

class Action(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Action>(Actions)

    var columnName by Actions.columnName
    var permissionType by Actions.permissionType
    var target by Actions.target
    var rowId by Actions.rowId
}