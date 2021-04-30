package com.leftindust.mockingbird.dao.impl

import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.dao.ClinicDao
import com.leftindust.mockingbird.graphql.types.GraphQLClinic
import com.leftindust.mockingbird.graphql.types.input.GraphQLClinicInput
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional


@Repository
@Transactional
class ClinicDaoImpl : ClinicDao {
    override fun addClinic(clinic: GraphQLClinicInput, requester: MediqToken): GraphQLClinic {
        TODO("Not yet implemented $clinic, $requester")
    }
}