package com.leftindust.mockingbird.dao

import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.dao.entity.Clinic
import com.leftindust.mockingbird.graphql.types.input.GraphQLClinicInput

interface ClinicDao {
    fun addClinic(clinic: GraphQLClinicInput, requester: MediqToken): Clinic
}
