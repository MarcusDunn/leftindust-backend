package com.leftindust.mockingbird.dao.entity

import com.leftindust.mockingbird.dao.entity.enums.FileDataType
import com.leftindust.mockingbird.dao.entity.superclasses.AbstractJpaPersistable
import java.sql.Blob
import javax.persistence.*

@Entity(name = "attachment")
class Attachment(
    @Lob
    @Column(name = "blob", columnDefinition = "BLOB NOT NULL")
    val blob: Blob,
    val name: String,
    @Enumerated(value = EnumType.STRING)
    val fileType: FileDataType
) : AbstractJpaPersistable()