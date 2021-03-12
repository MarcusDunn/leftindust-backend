package com.leftindust.mockingbird.dao.entity.converters

import com.leftindust.mockingbird.graphql.types.icd.FoundationIcdCode
import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Converter
class IcdCodeConverter : AttributeConverter<FoundationIcdCode, String> {
    override fun convertToDatabaseColumn(attribute: FoundationIcdCode): String {
        return attribute.value
    }

    override fun convertToEntityAttribute(dbData: String): FoundationIcdCode {
        return FoundationIcdCode(dbData)
    }
}