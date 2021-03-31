package com.leftindust.mockingbird.extensions

import biweekly.property.DateOrDateTimeProperty
import biweekly.property.DurationProperty
import java.sql.Timestamp


operator fun DateOrDateTimeProperty.plus(duration: DurationProperty): Timestamp {
    return Timestamp(this.value.toInstant().epochSecond * 1000 + duration.value.toMillis())
}

fun <T> timed(name: String? = null, repeat: Int? = null, func: () -> T): T {
    var ret: T? = null
    for (i in 0 until (repeat ?: 1)) {
        val start = System.currentTimeMillis();
        ret = func()
        if (name == null) {
            println("time taken: ${System.currentTimeMillis() - start}ms")
        } else {
            println("time taken for $name: ${System.currentTimeMillis() - start}ms")
        }
    }
    return ret!!
}

suspend fun <T> coTimed(name: String? = null, repeat: Int? = null, func: suspend () -> T): T {
    var ret: T? = null
    for (i in 0 until (repeat ?: 1)) {
        val start = System.currentTimeMillis();
        ret = func()
        if (name == null) {
            println("time taken: ${System.currentTimeMillis() - start}ms")
        } else {
            println("time taken for $name: ${System.currentTimeMillis() - start}ms")
        }
    }
    return ret!!
}