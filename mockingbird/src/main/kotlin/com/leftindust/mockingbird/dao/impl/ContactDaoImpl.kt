package com.leftindust.mockingbird.dao.impl

import com.leftindust.mockingbird.auth.Authorizer
import com.leftindust.mockingbird.auth.Crud
import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.dao.*
import com.leftindust.mockingbird.dao.entity.Action
import com.leftindust.mockingbird.dao.entity.EmergencyContact
import com.leftindust.mockingbird.dao.impl.repository.HibernatePatientRepository
import com.leftindust.mockingbird.extensions.CustomResult
import com.leftindust.mockingbird.extensions.Failure
import com.leftindust.mockingbird.extensions.Success
import com.leftindust.mockingbird.extensions.getOneOrNull
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
    ): CustomResult<List<EmergencyContact>, OrmFailureReason> {
        val readPatient = Action(Crud.READ to Tables.Patient)
        return if (requester can readPatient) {
            val contacts = patientRepository.getOneOrNull(pid)?.contacts
                ?: return Failure(DoesNotExist().also { logger.error("attempt to get nonexistent patient") })
            Success(contacts.toList())
        } else {
            logger.warn("unauthenticated attempt to READ to Patient")
            return Failure(NotAuthorized(requester))
        }
    }
}