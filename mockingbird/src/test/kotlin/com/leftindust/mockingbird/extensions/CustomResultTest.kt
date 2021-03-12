package com.leftindust.mockingbird.extensions

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

class CustomResultTest {

    @Test
    internal fun `test create success`() {
        fun returnsCustomResult(): CustomResult<String, String> {
            return Success("inner string")
        }

        when (returnsCustomResult()) {
            is Success -> return
            is Failure -> fail { "Fell through to failure branch on a Success CustomResult" }
        }
    }

    @Test
    internal fun testCreateFailure() {
        fun returnsCustomResult(): CustomResult<String, String> {
            return Failure("inner string")
        }

        when (returnsCustomResult()) {
            is Failure -> return
            is Success -> fail { "Fell through to Success branch on a Failure CustomResult" }
        }
    }


}