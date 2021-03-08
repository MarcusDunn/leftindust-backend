package com.leftindust.caper.sql.tables

import com.leftindust.caper.graphql.query.Users
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object MediqUsers : IntIdTable() {
    val uid = varchar("unique_id", 255).uniqueIndex()
    val settingsJson = varchar("settings_json", 1000)
    val settingsVersion = integer("settings_version")
}

class MediqUser(id: EntityID<Int>) : IntEntity(id) {
    fun toGql() = Users.GqlUser(uid = uid, settingsJson = settingsJson, settingsVersion = settingsVersion)


    companion object : IntEntityClass<MediqUser>(MediqUsers)

    var uid by MediqUsers.uid
    var settingsJson by MediqUsers.settingsJson
    var settingsVersion by MediqUsers.settingsVersion
}