package com.leftindust.mediq.dao.impl

import com.leftindust.mediq.auth.Authorizer
import com.leftindust.mediq.auth.Crud
import com.leftindust.mediq.auth.MediqToken
import com.leftindust.mediq.dao.*
import com.leftindust.mediq.dao.entity.Action
import com.leftindust.mediq.dao.entity.Visit
import com.leftindust.mediq.dao.impl.repository.HibernateDoctorRepository
import com.leftindust.mediq.dao.impl.repository.HibernatePatientRepository
import com.leftindust.mediq.dao.impl.repository.HibernateVisitRepository
import com.leftindust.mediq.extensions.CustomResult
import com.leftindust.mediq.extensions.Failure
import com.leftindust.mediq.extensions.Success
import com.leftindust.mediq.extensions.toInt
import com.leftindust.mediq.graphql.types.GraphQLVisitInput
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityNotFoundException

@Repository
@Transactional
class VisitDaoImpl(
    private val visitRepository: HibernateVisitRepository,
    private val doctorRepository: HibernateDoctorRepository,
    private val patientRepository: HibernatePatientRepository,
    authorizer: Authorizer,
) : VisitDao, AbstractHibernateDao(authorizer) {
    val logger: Logger = LogManager.getLogger()

    override suspend fun getVisitsForPatientPid(
        pid: Int,
        requester: MediqToken
    ): CustomResult<List<Visit>, OrmFailureReason> {
        return authenticateAndThen(requester, Crud.READ to Tables.Visit) {
            if (patientRepository.getPatientByPid(pid) == null) {
                null
            } else {
                visitRepository.getAllByPatientPid(pid)
            }
        }
    }

    override suspend fun getVisitByVid(vid: Long, requester: MediqToken): CustomResult<Visit, OrmFailureReason> {
        val readVisits = Action(Crud.READ to Tables.Visit)

        return if (requester can readVisits) {
            val visit = try {
                visitRepository.getOne(vid)
            } catch (e: JpaObjectRetrievalFailureException) {
                logger.error("attempted to find a non-existent visit with vid: $vid ")
                return Failure(DoesNotExist())
            }
            Success(visit)
        } else {
            logger.warn("unauthorized attempt to getVisitByVid")
            Failure(NotAuthorized(requester))
        }
    }

    override suspend fun getVisitsByDoctor(
        did: Long,
        requester: MediqToken
    ): CustomResult<List<Visit>, OrmFailureReason> {
        return authenticateAndThen(requester, Crud.READ to Tables.Visit) {
            try {
                doctorRepository.getOne(did)
            } catch (e: EntityNotFoundException) {
                return Failure(DoesNotExist("doctor with id: $did not found"))
            }
            visitRepository.getAllByDoctorId(did)
        }
    }

    override suspend fun addVisit(
        visitInput: GraphQLVisitInput,
        requester: MediqToken
    ): CustomResult<Visit, OrmFailureReason> {
        val requiredPermissions = listOf(
            Crud.READ to Tables.Doctor,
            Crud.READ to Tables.Patient,
            Crud.CREATE to Tables.Visit,
        ).map { Action(it) }

        return if (requester has requiredPermissions) {
            val doctor = doctorRepository.getByDid(visitInput.doctorId.toInt())
                ?: return Failure(DoesNotExist("no doc with that did"))
            val patient = patientRepository.getPatientByPid(visitInput.patientId.toInt())
                ?: return Failure(DoesNotExist("no patient with that pid"))
            Success(visitRepository.save(Visit(visitInput, patient, doctor)))
        } else {
            Failure(NotAuthorized(requester))
        }
    }
}