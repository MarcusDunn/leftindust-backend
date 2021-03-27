package com.leftindust.mockingbird.graphql.types

import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.expediagroup.graphql.generator.annotations.GraphQLName
import com.expediagroup.graphql.generator.scalars.ID
import com.leftindust.mockingbird.auth.Crud
import com.leftindust.mockingbird.dao.Tables
import com.leftindust.mockingbird.dao.entity.Action

@GraphQLName("Permission")
data class GraphQLPermission(
    val referencedTableName: Tables,
    val permissionType: Crud,
    val startTime: GraphQLTime? = null,
    val endTime: GraphQLTime? = null,
    val rowId: ID? = null,
    val columnName: String? = null,
) {

    @GraphQLIgnore
    fun toActionEntity(): Action {
        return Action(this)
    }

    fun friendlyName(): String {
        return StringBuilder().apply {
            append("${permissionType.name} to ${referencedTableName.name} ")
            if (startTime != null) {
                append("between ${startTime.unixMilliseconds} ")
                if (endTime != null) {
                    append("to ${endTime.unixMilliseconds} ")
                } else {
                    append("and forever")
                }
            } else {
                if (endTime != null) {
                    append("before $endTime ")
                }
            }
            if (rowId != null) {
                append("on row with primary key $rowId")
            }
            if (columnName != null) {
                append("on the $columnName column")
            }
        }.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is GraphQLPermission) return false

        if (referencedTableName != other.referencedTableName) return false
        if (permissionType != other.permissionType) return false
        if (startTime != other.startTime) return false
        if (endTime != other.endTime) return false
        if (rowId != other.rowId) return false
        if (columnName != other.columnName) return false

        return true
    }

    override fun hashCode(): Int {
        var result = referencedTableName.hashCode()
        result = 31 * result + permissionType.hashCode()
        result = 31 * result + (startTime?.hashCode() ?: 0)
        result = 31 * result + (endTime?.hashCode() ?: 0)
        result = 31 * result + (rowId?.hashCode() ?: 0)
        result = 31 * result + (columnName?.hashCode() ?: 0)
        return result
    }

    constructor(action: Action) : this(
        referencedTableName = action.referencedTableName,
        permissionType = action.permissionType,
        startTime = action.startTime?.let { GraphQLTime(it) },
        endTime = action.endTime?.let { GraphQLTime(it) },
        rowId = action.rowId?.let { ID(it.toString()) }, // let needed as toString on null is a valid call
        columnName = action.columnName,
    )

}