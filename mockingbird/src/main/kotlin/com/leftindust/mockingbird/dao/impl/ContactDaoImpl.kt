package com.leftindust.mockingbird.dao.impl

import com.leftindust.mockingbird.auth.Authorizer
import com.leftindust.mockingbird.auth.Crud
import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.auth.NotAuthorizedException
import com.leftindust.mockingbird.dao.ContactDao
import com.leftindust.mockingbird.dao.Tables
import com.leftindust.mockingbird.dao.entity.EmergencyContact
import com.leftindust.mockingbird.dao.impl.repository.HibernatePatientRepository
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Transactional
@Repository
class ContactDaoImpl(
    @Autowired authorizer: Authorizer,
    @Autowired private val patientRepository: HibernatePatientRepository,
) : AbstractHibernateDao(authorizer), ContactDao {

    private val logger = LogManager.getLogger()

    override suspend fun getByPatient(
        pid: Long,
        requester: MediqToken
    ): Collection<EmergencyContact> {
        return if (requester can (Crud.READ to Tables.Patient)) {
            patientRepository.getOne(pid).contacts
        } else {
            throw NotAuthorizedException(requester, Crud.READ to Tables.Patient)
        }
    }
}