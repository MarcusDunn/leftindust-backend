package com.leftindust.mockingbird.dao.entity.converters

import com.leftindust.mockingbird.graphql.types.icd.FoundationIcdCode
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class IcdCodeConverterTest {
    private val icdCodeConverter = IcdCodeConverter()

    @Test
    fun convertToDatabaseColumn() {
        val url = "123321"
        val actual = icdCodeConverter.convertToDatabaseColumn(FoundationIcdCode(url))
        assertEquals(url, actual)
    }

    @Test
    fun convertToEntityAttribute() {
        val dbData = "123321"
        val actual = icdCodeConverter.convertToEntityAttribute(dbData)
        assertEquals(FoundationIcdCode(dbData), actual)
    }
}