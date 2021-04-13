package com.leftindust.mockingbird.graphql.types

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.sql.Timestamp

internal class GraphQLTimeTest {

    @Test
    fun withRespectTo() {
        val gqlTime = GraphQLTime(unixMilliseconds = Timestamp.valueOf("2018-09-01 09:01:15").time)

        val unixMilliseconds = GraphQLTime.TimeZonedTime(
            "America/Los_Angeles",
            Timestamp.valueOf("2018-09-01 02:01:15").time
        ).unixMilliseconds

        val unixMilliseconds1 = gqlTime.withRespectTo("America/Los_Angeles").unixMilliseconds

        assertEquals(
            unixMilliseconds,
            unixMilliseconds1,
        )
    }
}