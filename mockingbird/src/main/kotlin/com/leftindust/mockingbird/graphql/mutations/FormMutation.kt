package com.leftindust.mockingbird.graphql.mutations

import com.expediagroup.graphql.server.operations.Mutation
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.FormDao
import com.leftindust.mockingbird.graphql.types.GraphQLFormTemplate
import com.leftindust.mockingbird.graphql.types.input.GraphQLFormTemplateInput
import org.springframework.stereotype.Component

@Component
class FormMutation(private val formDao: FormDao) : Mutation {
    suspend fun addForm(form: GraphQLFormTemplateInput, authContext: GraphQLAuthContext): GraphQLFormTemplate {
        return GraphQLFormTemplate(formDao.addForm(form, authContext.mediqAuthToken), authContext)
    }

    suspend fun deleteForm(form: GraphQLFormTemplate.ID, authContext: GraphQLAuthContext): GraphQLFormTemplate {
        return GraphQLFormTemplate(formDao.deleteForm(form, authContext.mediqAuthToken), authContext)
    }
}