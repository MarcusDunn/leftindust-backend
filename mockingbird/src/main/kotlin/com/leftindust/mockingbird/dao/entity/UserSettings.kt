package com.leftindust.mockingbird.dao.entity

import com.google.gson.JsonObject
import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
class UserSettings(
    @Column(name = "settings_version", nullable = false)
    var version: Int,
    @Column(name = "settings_json", nullable = false)
    var settingsJSON: String = JsonObject().toString(),
)