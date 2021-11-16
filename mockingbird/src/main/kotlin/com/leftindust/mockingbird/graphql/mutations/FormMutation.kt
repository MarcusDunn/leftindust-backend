package com.leftindust.mockingbird.graphql.mutations

import com.expediagroup.graphql.server.operations.Mutation
import com.google.gson.JsonParser.parseString
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.FormDao
import com.leftindust.mockingbird.dao.FormDataDao
import com.leftindust.mockingbird.dao.PatientDao
import com.leftindust.mockingbird.graphql.types.GraphQLFormData
import com.leftindust.mockingbird.graphql.types.GraphQLFormTemplate
import com.leftindust.mockingbird.graphql.types.GraphQLPatient
import com.leftindust.mockingbird.graphql.types.input.GraphQLFormTemplateInput
import org.springframework.stereotype.Component

@Component
class FormMutation(
    private val formDao: FormDao,
    private val formDataDao: FormDataDao,
    private val patientDao: PatientDao
) : Mutation {
    suspend fun addSurveyTemplate(
        surveyTemplate: GraphQLFormTemplateInput,
        authContext: GraphQLAuthContext
    ): GraphQLFormTemplate {
        return GraphQLFormTemplate(formDao.addForm(surveyTemplate, authContext.mediqAuthToken), authContext)
    }

    suspend fun submitSurvey(
        patient: GraphQLPatient.ID,
        surveyJson: String,
        authContext: GraphQLAuthContext
    ): GraphQLFormData {
        val attachedForm = formDataDao.attachForm(patient, form = parseString(surveyJson), authContext.mediqAuthToken)
        return GraphQLFormData(attachedForm.data.toString(), patient, authContext)
    }

    suspend fun assignSurvey(
        patients: List<GraphQLPatient.ID>,
        survey: GraphQLFormTemplate.ID,
        authContext: GraphQLAuthContext,
    ): List<GraphQLPatient> {
        val patientEntities = patientDao.assignForms(patients, survey, authContext.mediqAuthToken)
        return patientEntities.map { GraphQLPatient(it, authContext) }
    }
}

