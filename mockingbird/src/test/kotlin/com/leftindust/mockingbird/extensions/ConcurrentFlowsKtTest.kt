package com.leftindust.mockingbird.extensions

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class ConcurrentFlowsKtTest {

    @Test
    fun parallelMap() {
        runBlocking {
            val someList = 0 until 20

            val startTimePMap = System.currentTimeMillis()
            val result = someList.parallelMap { delay(100); it * 2 }
            val timeTookPMap = System.currentTimeMillis() - startTimePMap
            println(timeTookPMap)
            assert(timeTookPMap < (2 * 100))

            val startTimeMap = System.currentTimeMillis()
            val expected = (0 until 20).map { delay(100); it * 2 }
            val timeTookMap = System.currentTimeMillis() - startTimeMap
            println(timeTookMap)
            assert(timeTookMap > 20 * 100)

            assertEquals(expected, result)
        }
    }
}