package com.leftindust.mockingbird.graphql.queries

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.exceptions.GraphQLKotlinException
import com.expediagroup.graphql.server.operations.Query
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.FormDao
import com.leftindust.mockingbird.graphql.types.GraphQLFormTemplate
import com.leftindust.mockingbird.graphql.types.input.GraphQLRangeInput
import org.springframework.stereotype.Component

@Component
class FormTemplateQuery(
    private val formDao: FormDao
) : Query {

    @GraphQLDescription("fetch survey templates by one getting a range or the survey id")
    suspend fun surveys(
        range: GraphQLRangeInput? = null,
        surveys: List<GraphQLFormTemplate.ID>? = null,
        authContext: GraphQLAuthContext
    ): List<GraphQLFormTemplate> {
        return when {
            range == null && surveys != null -> {
                formDao.getByIds(surveys, authContext.mediqAuthToken)
            }
            range != null && surveys == null -> {
                formDao.getMany(range, authContext.mediqAuthToken)
            }
            else -> throw GraphQLKotlinException("invalid argument combination to surveys")
        }.map { GraphQLFormTemplate(it, authContext) }
    }
}