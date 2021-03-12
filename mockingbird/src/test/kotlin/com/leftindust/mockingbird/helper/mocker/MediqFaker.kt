package com.leftindust.mockingbird.helper.mocker

import io.github.serpro69.kfaker.Faker
import io.github.serpro69.kfaker.FakerConfig
import io.github.serpro69.kfaker.create
import java.util.*

abstract class MediqFaker<T>(seed: Long) {
    val seededRandom = Random(seed)

    val faker = Faker(FakerConfig.builder().create {
        random = seededRandom
    })

    abstract fun create(): T

    infix fun perhapsNullWithOddsOf(odds: Int): T? {
        return if (seededRandom.nextInt(100) < odds) {
            create()
        } else {
            null
        }
    }

    operator fun invoke(): T {
        return create()
    }
}
