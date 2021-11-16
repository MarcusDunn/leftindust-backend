package com.leftindust.mockingbird.dao

import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.dao.entity.AssignedForm
import com.leftindust.mockingbird.dao.entity.Form
import com.leftindust.mockingbird.graphql.types.GraphQLFormTemplate
import com.leftindust.mockingbird.graphql.types.GraphQLPatient
import com.leftindust.mockingbird.graphql.types.input.GraphQLFormTemplateInput
import com.leftindust.mockingbird.graphql.types.input.GraphQLRangeInput

interface FormDao {
    suspend fun getByIds(ids: List<GraphQLFormTemplate.ID>, requester: MediqToken): Collection<Form>
    suspend fun getMany(range: GraphQLRangeInput, requester: MediqToken): List<Form>
    suspend fun addForm(form: GraphQLFormTemplateInput, requester: MediqToken): Form
    suspend fun deleteForm(form: GraphQLFormTemplate.ID, requester: MediqToken): Form
    suspend fun getByPatientAssigned(patient: GraphQLPatient.ID, requester: MediqToken): Collection<AssignedForm>
}
