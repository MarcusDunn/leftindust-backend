package com.leftindust.mockingbird.dao.entity

import com.leftindust.mockingbird.auth.Crud
import com.leftindust.mockingbird.dao.Tables
import com.leftindust.mockingbird.dao.entity.superclasses.AbstractJpaPersistable
import com.leftindust.mockingbird.graphql.types.input.GraphQLPermissionInput
import java.sql.Timestamp
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated

@Entity
class Action(
    @Column(name = "referenced_table_name")
    @Enumerated(EnumType.STRING)
    val referencedTableName: Tables,
    @Enumerated(EnumType.STRING)
    @Column(name = "permission_type")
    val permissionType: Crud,
    @Column(name = "start_time")
    val startTime: Timestamp? = null,
    @Column(name = "end_time")
    val endTime: Timestamp? = null,
    @Column(name = "row_id")
    val rowId: UUID? = null,
    @Column(name = "column_name")
    val columnName: String? = null,
) : AbstractJpaPersistable() {

    constructor(pair: Pair<Crud, Tables>) : this(
        permissionType = pair.first,
        referencedTableName = pair.second,
    )

    constructor(graphQLPermission: GraphQLPermissionInput) : this(
        referencedTableName = graphQLPermission.referencedTableName,
        permissionType = graphQLPermission.permissionType,
        startTime = graphQLPermission.startTime?.toTimestamp(),
        endTime = graphQLPermission.endTime?.toTimestamp(),
        rowId = graphQLPermission.rowId,
        columnName = graphQLPermission.columnName,
    )

    infix fun isSuperset(action: Action): Boolean {
        return referencedTableName eqOrLhsIsNull action.referencedTableName &&
                permissionType eqOrLhsIsNull action.permissionType &&
                startTime gtOrLhsIsNull action.startTime &&
                endTime ltOrLhsIsNull action.endTime &&
                rowId eqOrLhsIsNull action.rowId &&
                columnName eqOrLhsIsNull action.columnName
    }

    private infix fun <T> T.eqOrLhsIsNull(rhs: T) = this == rhs || this == null

    private infix fun Timestamp?.ltOrLhsIsNull(rhs: Timestamp?): Boolean {
        return this?.time == rhs?.time || this == null || this.time < rhs?.time ?: return false
    }

    private infix fun Timestamp?.gtOrLhsIsNull(rhs: Timestamp?): Boolean {
        return this?.time == rhs?.time || this == null || this.time < rhs?.time ?: return false
    }
}

