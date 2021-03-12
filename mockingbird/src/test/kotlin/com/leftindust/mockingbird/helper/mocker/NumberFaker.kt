package com.leftindust.mockingbird.helper.mocker

import kotlin.math.absoluteValue
import kotlin.random.Random

class NumberFaker(seed: Long) : MediqFaker<Int>(seed) {
    private val rand = Random(seed)

    override fun create(): Int {
        return rand.nextInt().absoluteValue
    }
}
