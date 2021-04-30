package com.leftindust.mockingbird.graphql.types

import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.expediagroup.graphql.generator.scalars.ID
import com.leftindust.mockingbird.dao.DoctorDao
import org.springframework.beans.factory.annotation.Autowired

data class GraphQLClinic(
    val cid: ID,
    val address: GraphQLAddress,
) {
    fun doctors(@GraphQLIgnore @Autowired doctorDao: DoctorDao): List<GraphQLDoctor> {
        TODO("$doctorDao")
    }
}
