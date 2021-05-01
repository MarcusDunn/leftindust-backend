package com.leftindust.mockingbird.graphql.types

import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.expediagroup.graphql.generator.annotations.GraphQLName
import com.expediagroup.graphql.generator.scalars.ID
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.DoctorDao
import com.leftindust.mockingbird.dao.entity.Clinic
import com.leftindust.mockingbird.extensions.gqlID
import org.springframework.beans.factory.annotation.Autowired

@GraphQLName("Clinic")
data class GraphQLClinic(
    val cid: ID,
    val address: GraphQLAddress,
    private val authContext: GraphQLAuthContext
) {
    constructor(clinic: Clinic, id: Long, authContext: GraphQLAuthContext) : this(
        cid = gqlID(id),
        address = GraphQLAddress(clinic.address),
        authContext = authContext,
    ) {
        assert(clinic.id == id)
    }

    fun doctors(@GraphQLIgnore @Autowired doctorDao: DoctorDao): List<GraphQLDoctor> {
        TODO("$doctorDao")
    }
}
