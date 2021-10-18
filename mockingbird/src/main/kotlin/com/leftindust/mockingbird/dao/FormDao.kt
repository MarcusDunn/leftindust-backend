package com.leftindust.mockingbird.dao

import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.dao.entity.Form
import com.leftindust.mockingbird.graphql.types.GraphQLDoctor
import com.leftindust.mockingbird.graphql.types.GraphQLFormTemplate

interface FormDao {
    suspend fun getById(id: GraphQLFormTemplate.ID, requester: MediqToken): Form
    suspend fun getByDoctorId(doctor: GraphQLDoctor.ID, requester: MediqToken): List<Form>
}
