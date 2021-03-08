package com.leftindust.mediq.helper.mocker

import java.sql.Timestamp
import java.time.Instant
import java.util.*
import kotlin.random.asKotlinRandom

class TimestampFaker(seed: Long) : MediqFaker<Timestamp>(seed) {
    companion object {
        const val SECONDS_IN_A_YEAR = 31556952L
        val MAX_SECONDS = Timestamp.valueOf("2020-01-01 00:00:00").time / 1000
        val MIN_SECONDS = Timestamp.valueOf("2010-01-01 00:00:00").time / 1000
    }

    override fun create(): Timestamp {

        return Timestamp.from(
            Instant.ofEpochSecond(
                seededRandom.asKotlinRandom().nextLong(MIN_SECONDS, MAX_SECONDS)
            )
        )
    }



    infix fun atLeastYearsAgo(years: Int): Timestamp {
        val max = Instant.now().epochSecond + SECONDS_IN_A_YEAR * years
        val time = seededRandom.asKotlinRandom().nextLong(MIN_SECONDS, max)
        return Timestamp.from(Instant.ofEpochSecond(time))
    }
}
