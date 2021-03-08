package com.leftindust.mediq.dao.entity.converters

import biweekly.Biweekly
import biweekly.ICalendar
import biweekly.component.VEvent
import biweekly.property.RecurrenceRule
import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Converter
class RecurrenceConverter : AttributeConverter<RecurrenceRule, String> {
    override fun convertToDatabaseColumn(attribute: RecurrenceRule?): String? {
        return Biweekly.write(ICalendar().apply {
            addEvent(
                VEvent()
                    .apply { recurrenceRule = attribute }
            )
        }).go()
    }

    override fun convertToEntityAttribute(dbData: String?): RecurrenceRule? {
        return Biweekly.parse(dbData).first().events.first().recurrenceRule
    }

}