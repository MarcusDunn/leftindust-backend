package com.leftindust.mockingbird.graphql.mutations

import com.expediagroup.graphql.server.operations.Mutation
import com.leftindust.mockingbird.dao.FormDao
import com.leftindust.mockingbird.graphql.types.GraphQLFormTemplate
import com.leftindust.mockingbird.graphql.types.input.GraphQLFormTemplateInput
import org.springframework.stereotype.Component

@Component
class FormMutation(private val formDao: FormDao) : Mutation {
    fun addForm(form: GraphQLFormTemplateInput): GraphQLFormTemplate {
        TODO()
    }

    fun deleteForm(form: GraphQLFormTemplate.ID): GraphQLFormTemplate {
        TODO()
    }
}