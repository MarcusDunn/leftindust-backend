package com.leftindust.mockingbird.graphql.queries

import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class ConverterQueryTest {

    @Test
    fun convert() {
        val inputJson = """
            |[
            |   {pid: 1, first_name: "marcus", cell_phone: {number: 123422, phoneType: Cell }},
            |   {pid: 2, first_name: "dan", cell_phone: {number: 12121, phoneType: Home }}
            |]
            |""".trimMargin()
        val expectedCsv = """
            |pid,first_name,cell_phone.number,cell_phone.phoneType,
            |1,"marcus",123422,"Cell",
            |2,"dan",12121,"Home",
            |""".trimMargin()
        val result = ConverterQuery().convert(inputJson, ConverterQuery.ConvertTarget.Csv, mockk())
        assertEquals(expectedCsv, result)
    }
}