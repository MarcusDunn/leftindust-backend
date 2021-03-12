package com.leftindust.mockingbird.helper.mocker

import com.leftindust.mockingbird.dao.entity.MediqGroup
import kotlin.math.absoluteValue

class GroupFaker(seed: Long) : MediqFaker<MediqGroup>(seed) {
    private val numberFaker = NumberFaker(seed)

    override fun create(): MediqGroup {
        return MediqGroup(
            gid = (numberFaker().absoluteValue.toString() + numberFaker().absoluteValue.toString()).toLong(),
            name = "Group ${faker.name.firstName()}",
        )
    }
}
