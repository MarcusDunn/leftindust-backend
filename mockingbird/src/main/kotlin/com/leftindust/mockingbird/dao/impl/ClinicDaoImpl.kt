package com.leftindust.mockingbird.dao.impl

import com.leftindust.mockingbird.auth.Authorizer
import com.leftindust.mockingbird.auth.Crud
import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.auth.NotAuthorizedException
import com.leftindust.mockingbird.dao.ClinicDao
import com.leftindust.mockingbird.dao.Tables
import com.leftindust.mockingbird.dao.entity.Clinic
import com.leftindust.mockingbird.dao.impl.repository.HibernateClinicRepository
import com.leftindust.mockingbird.graphql.types.input.GraphQLClinicInput
import org.hibernate.SessionFactory
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional


@Repository
@Transactional
class ClinicDaoImpl(
    private val clinicRepository: HibernateClinicRepository,
    private val sessionFactory: SessionFactory,
    authorizer: Authorizer
) : ClinicDao,
    AbstractHibernateDao(authorizer) {
    override suspend fun addClinic(clinic: GraphQLClinicInput, requester: MediqToken): Clinic {
        val createClinic = Crud.CREATE to Tables.Clinic
        return if (requester can createClinic) {
            clinicRepository.save(
                Clinic(clinic, sessionFactory.currentSession)
            )
        } else {
            throw NotAuthorizedException(requester, createClinic)
        }
    }
}