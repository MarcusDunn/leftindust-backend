package com.leftindust.mockingbird.dao

import com.google.gson.JsonElement
import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.dao.entity.FormData
import com.leftindust.mockingbird.graphql.types.GraphQLPatient

interface FormDataDao {
    suspend fun attachForm(patient: GraphQLPatient.ID, form: JsonElement, requester: MediqToken): FormData
    suspend fun getForms(patient: GraphQLPatient.ID, requester: MediqToken): List<FormData>
}