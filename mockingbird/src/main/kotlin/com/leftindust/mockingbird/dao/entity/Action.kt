package com.leftindust.mockingbird.dao.entity

import com.leftindust.mockingbird.auth.Crud
import com.leftindust.mockingbird.dao.Tables
import com.leftindust.mockingbird.dao.entity.superclasses.AbstractJpaPersistable
import com.leftindust.mockingbird.extensions.toLong
import com.leftindust.mockingbird.graphql.types.GraphQLPermission
import java.sql.Timestamp
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
    val rowId: Long? = null,
    @Column(name = "column_name")
    val columnName: String? = null,
) : AbstractJpaPersistable<Long>() {

    constructor(pair: Pair<Crud, Tables>) : this(
        permissionType = pair.first,
        referencedTableName = pair.second,
    )

    constructor(graphQLPermission: GraphQLPermission) : this(
        referencedTableName = graphQLPermission.referencedTableName,
        permissionType = graphQLPermission.permissionType,
        startTime = graphQLPermission.startTime?.toTimestamp(),
        endTime = graphQLPermission.endTime?.toTimestamp(),
        rowId = graphQLPermission.rowId?.toLong(),
        columnName = graphQLPermission.columnName,
    )

    infix fun isSuperset(action: Action): Boolean {
        return referencedTableName eqOrRhsIsNull action.referencedTableName &&
                permissionType eqOrRhsIsNull action.permissionType &&
                startTime gtOrRhsIsNull action.startTime &&
                endTime ltOrRhsIsNull action.endTime &&
                rowId eqOrRhsIsNull action.rowId &&
                columnName eqOrRhsIsNull action.columnName
    }

    private infix fun <T> T.eqOrRhsIsNull(rhs: T) = this == rhs || this == null

    private infix fun Timestamp?.ltOrRhsIsNull(rhs: Timestamp?): Boolean {
        return this?.time == rhs?.time || rhs == null || (this?.time ?: return false) < rhs.time
    }

    private infix fun Timestamp?.gtOrRhsIsNull(rhs: Timestamp?): Boolean {
        return this?.time == rhs?.time || rhs == null || (this?.time ?: return false) < rhs.time
    }
}

