package com.leftindust.caper.sql.tables

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object AccessControlLists : IntIdTable() {
    val actionId = integer("action_id").references(Actions.id)
    val groupId = integer("group_id").references(Groups.id)
    val mediqUser = integer("mediq_user").references(MediqUsers.id)
}


class AccessControlList(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<AccessControlList>(AccessControlLists)

    var actionId by AccessControlLists.actionId
    var groupId by AccessControlLists.groupId
    var mediqUser by AccessControlLists.mediqUser
}