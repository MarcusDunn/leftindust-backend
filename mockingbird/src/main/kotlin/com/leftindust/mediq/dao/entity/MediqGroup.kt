package com.leftindust.mediq.dao.entity

import com.expediagroup.graphql.annotations.GraphQLName
import com.leftindust.mediq.dao.entity.superclasses.AbstractJpaPersistable
import javax.persistence.Entity

@Entity(name = "mediq_group")
class MediqGroup(
    val gid: Long,
    val name: String,
) : AbstractJpaPersistable<Long>() {
    @GraphQLName("Group")
    data class GraphQL(
        val name: String
    )


    override fun toString(): String {
        return "Group(name='$name')"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MediqGroup

        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + name.hashCode()
        return result
    }
}