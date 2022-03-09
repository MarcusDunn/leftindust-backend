package com.leftindust.mockingbird.dao.form.feild

import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.dao.entity.FormField
import com.leftindust.mockingbird.graphql.types.GraphQLFormSection
import com.leftindust.mockingbird.graphql.types.GraphQlFormField

interface ReadFormFieldDao {
    suspend fun getSectionFields(id: GraphQLFormSection.ID, authContext: MediqToken): List<FormField>
    suspend fun getFormFieldMultiSelectPossibilities(ffid: GraphQlFormField.ID, mediqAuthToken: MediqToken): List<String>?
}
