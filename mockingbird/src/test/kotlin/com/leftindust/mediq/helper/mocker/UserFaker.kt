package com.leftindust.mediq.helper.mocker

import com.google.gson.JsonObject
import com.leftindust.mediq.dao.entity.MediqUser
import com.leftindust.mediq.dao.entity.UserSettings

class UserFaker(private val seed: Long) : MediqFaker<MediqUser>(seed) {
    override fun create(): MediqUser {
        return MediqUser(
            uniqueId = NumberFaker(seed).create().toString(),
            group = GroupFaker(seed) perhapsNullWithOddsOf 50,
            settings = UserSettings(1, JsonObject().toString())
        )
    }
}
