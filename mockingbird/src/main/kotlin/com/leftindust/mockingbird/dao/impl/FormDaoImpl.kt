package com.leftindust.mockingbird.dao.impl

import com.leftindust.mockingbird.auth.Authorizer
import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.dao.FormDao
import com.leftindust.mockingbird.dao.entity.Form
import com.leftindust.mockingbird.dao.impl.repository.HibernateFormRepository
import com.leftindust.mockingbird.extensions.getByIds
import com.leftindust.mockingbird.graphql.types.GraphQLDoctor
import com.leftindust.mockingbird.graphql.types.GraphQLFormTemplate
import com.leftindust.mockingbird.graphql.types.input.GraphQLRangeInput
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import javax.transaction.Transactional

@Repository
@Transactional
class FormDaoImpl(
    @Autowired private val formRepository: HibernateFormRepository,
    @Autowired authorizer: Authorizer
) : FormDao, AbstractHibernateDao(authorizer) {
    override suspend fun getByIds(ids: List<GraphQLFormTemplate.ID>, requester: MediqToken): Collection<Form> {
        return formRepository.getByIds(ids.map { it.id })
    }

    override suspend fun getMany(range: GraphQLRangeInput, requester: MediqToken): List<Form> {
        return formRepository.findAll(range.toPageable()).toList()
    }
}