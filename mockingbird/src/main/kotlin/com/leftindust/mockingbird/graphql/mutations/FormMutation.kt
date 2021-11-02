package com.leftindust.mockingbird.graphql.mutations

import com.expediagroup.graphql.server.operations.Mutation
import com.google.gson.JsonParser.parseString
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.FormDao
import com.leftindust.mockingbird.dao.FormDataDao
import com.leftindust.mockingbird.graphql.types.GraphQLFormData
import com.leftindust.mockingbird.graphql.types.GraphQLFormTemplate
import com.leftindust.mockingbird.graphql.types.GraphQLPatient
import com.leftindust.mockingbird.graphql.types.input.GraphQLFormTemplateInput
import org.springframework.stereotype.Component

@Component
class FormMutation(private val formDao: FormDao, private val formDataDao: FormDataDao) : Mutation {
    suspend fun addFormTemplate(form: GraphQLFormTemplateInput, authContext: GraphQLAuthContext): GraphQLFormTemplate {
        return GraphQLFormTemplate(formDao.addForm(form, authContext.mediqAuthToken), authContext)
    }

    suspend fun submitForm(
        patient: GraphQLPatient.ID,
        formJson: String,
        authContext: GraphQLAuthContext
    ): GraphQLFormData {
        val attachedForm = formDataDao.attachForm(patient, form = parseString(formJson), authContext.mediqAuthToken)
        return GraphQLFormData(attachedForm.toString(), patient, authContext)
    }
}

