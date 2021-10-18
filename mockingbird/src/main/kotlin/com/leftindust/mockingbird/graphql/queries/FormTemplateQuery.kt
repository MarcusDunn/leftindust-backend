package com.leftindust.mockingbird.graphql.queries

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.exceptions.GraphQLKotlinException
import com.expediagroup.graphql.server.operations.Query
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.FormDao
import com.leftindust.mockingbird.graphql.types.GraphQLDoctor
import com.leftindust.mockingbird.graphql.types.GraphQLFormTemplate
import org.springframework.stereotype.Component

@Component
class FormTemplateQuery(
    private val formDao: FormDao
) : Query {

    @GraphQLDescription("fetch form templates by one of the doctor who created them or the form id")
    suspend fun forms(
        doctor: GraphQLDoctor.ID? = null,
        form: GraphQLFormTemplate.ID? = null,
        authContext: GraphQLAuthContext
    ): List<GraphQLFormTemplate> {
        return when {
            doctor == null && form != null -> {
                val formEntity = formDao.getById(form, authContext.mediqAuthToken)
                val gqlForm = GraphQLFormTemplate(formEntity, authContext)
                listOf(gqlForm)
            }
            doctor != null && form == null -> {
                val formEntities = formDao.getByDoctorId(doctor, authContext.mediqAuthToken)
                formEntities.map { GraphQLFormTemplate(it, authContext) }
            }
            else -> throw GraphQLKotlinException("invalid argument combination to forms")
        }
    }
}