package com.leftindust.mockingbird.extensions

import biweekly.property.DateOrDateTimeProperty
import biweekly.property.DurationProperty
import java.sql.Timestamp


operator fun DateOrDateTimeProperty.plus(duration: DurationProperty): Timestamp {
    return Timestamp(this.value.toInstant().epochSecond * 1000 + duration.value.toMillis())
}