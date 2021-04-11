package com.leftindust.mockingbird.dao.entity

import com.leftindust.mockingbird.dao.entity.converters.IcdCodeConverter
import com.leftindust.mockingbird.dao.entity.superclasses.AbstractJpaPersistable
import com.leftindust.mockingbird.graphql.types.GraphQLVisitInput
import com.leftindust.mockingbird.graphql.types.icd.FoundationIcdCode
import javax.persistence.Column
import javax.persistence.Convert
import javax.persistence.Entity
import javax.persistence.OneToOne

@Entity
class Visit(
    @OneToOne
    var event: Event,
    var title: String? = null,
    var description: String? = null,
    @Column(name = "icd_foundation_code", nullable = false)
    @Convert(converter = IcdCodeConverter::class)
    var icdFoundationCode: FoundationIcdCode,
) : AbstractJpaPersistable<Long>() {

    constructor(visitInput: GraphQLVisitInput, event: Event) : this(
        event = event,
        title = visitInput.title,
        description = visitInput.description,
        icdFoundationCode = FoundationIcdCode(visitInput.foundationIcdCode),
    )
}