package com.leftindust.mockingbird.graphql.queries

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.exceptions.GraphQLKotlinException
import com.expediagroup.graphql.server.operations.Query
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.FormDao
import com.leftindust.mockingbird.graphql.types.GraphQLDoctor
import com.leftindust.mockingbird.graphql.types.GraphQLFormTemplate
import com.leftindust.mockingbird.graphql.types.input.GraphQLRangeInput
import org.springframework.stereotype.Component

@Component
class FormTemplateQuery(
    private val formDao: FormDao
) : Query {

    @GraphQLDescription("fetch form templates by one getting a range or the form id")
    suspend fun forms(
        range: GraphQLRangeInput? = null,
        forms: List<GraphQLFormTemplate.ID>? = null,
        authContext: GraphQLAuthContext
    ): List<GraphQLFormTemplate> {
        return when {
            range == null && forms != null -> {
                formDao.getByIds(forms, authContext.mediqAuthToken)
            }
            range != null && forms == null -> {
                formDao.getMany(range, authContext.mediqAuthToken)
            }
            else -> throw GraphQLKotlinException("invalid argument combination to forms")
        }.map { GraphQLFormTemplate(it, authContext) }
    }
}