package com.leftindust.mockingbird.dao.impl

import com.leftindust.mockingbird.auth.Authorizer
import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.dao.FormDao
import com.leftindust.mockingbird.dao.entity.Form
import com.leftindust.mockingbird.dao.impl.repository.HibernateFormRepository
import com.leftindust.mockingbird.graphql.types.GraphQLDoctor
import com.leftindust.mockingbird.graphql.types.GraphQLFormTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import javax.transaction.Transactional

@Repository
@Transactional
class FormDaoImpl(
    @Autowired private val formRepository: HibernateFormRepository,
    @Autowired authorizer: Authorizer
) : FormDao, AbstractHibernateDao(authorizer) {
    override suspend fun getById(id: GraphQLFormTemplate.ID, requester: MediqToken): Form {
        TODO("Not yet implemented")
    }

    override suspend fun getByDoctorId(doctor: GraphQLDoctor.ID, requester: MediqToken): List<Form> {
        TODO("Not yet implemented")
    }
}