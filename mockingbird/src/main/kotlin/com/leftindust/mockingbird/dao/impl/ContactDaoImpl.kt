package com.leftindust.mockingbird.dao.impl

import com.leftindust.mockingbird.auth.Authorizer
import com.leftindust.mockingbird.auth.Crud
import com.leftindust.mockingbird.auth.Crud.READ
import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.auth.NotAuthorizedException
import com.leftindust.mockingbird.dao.ContactDao
import com.leftindust.mockingbird.dao.Tables
import com.leftindust.mockingbird.dao.Tables.Patient
import com.leftindust.mockingbird.dao.entity.EmergencyContact
import com.leftindust.mockingbird.dao.impl.repository.HibernateContactRepository
import com.leftindust.mockingbird.graphql.types.GraphQLPatient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Transactional
@Repository
class ContactDaoImpl(
    @Autowired authorizer: Authorizer,
    @Autowired private val contactRepository: HibernateContactRepository,
) : AbstractHibernateDao(authorizer), ContactDao {

    override suspend fun getPatientContacts(
        pid: GraphQLPatient.ID,
        requester: MediqToken
    ): Collection<EmergencyContact> {
        return if (requester can (READ to Patient)) {
            contactRepository.getAllByPatient_Id(pid.id)
        } else {
            throw NotAuthorizedException(requester, READ to Patient)
        }
    }
}