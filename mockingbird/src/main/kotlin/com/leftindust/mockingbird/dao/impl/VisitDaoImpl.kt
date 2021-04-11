package com.leftindust.mockingbird.dao.impl

import com.leftindust.mockingbird.auth.Authorizer
import com.leftindust.mockingbird.auth.Crud
import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.dao.*
import com.leftindust.mockingbird.dao.entity.Action
import com.leftindust.mockingbird.dao.entity.Visit
import com.leftindust.mockingbird.dao.impl.repository.HibernateDoctorRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernateEventRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernatePatientRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernateVisitRepository
import com.leftindust.mockingbird.extensions.*
import com.leftindust.mockingbird.graphql.types.GraphQLVisitInput
import com.leftindust.mockingbird.graphql.types.examples.GraphQLVisitExample
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager
import javax.persistence.EntityNotFoundException

@Repository
@Transactional
class VisitDaoImpl(
    authorizer: Authorizer,
    private val eventRepository: HibernateEventRepository,
    private val visitRepository: HibernateVisitRepository,
    private val doctorRepository: HibernateDoctorRepository,
    private val patientRepository: HibernatePatientRepository,
    private val entityManager: EntityManager,
) : VisitDao, AbstractHibernateDao(authorizer) {
    private val logger: Logger = LogManager.getLogger()

    override suspend fun getVisitsForPatientPid(
        pid: Long,
        requester: MediqToken
    ): CustomResult<List<Visit>, OrmFailureReason> {
        return authenticateAndThen(requester, Crud.READ to Tables.Visit) {
            if (patientRepository.getOneOrNull(pid) == null) {
                null
            } else {
                visitRepository.getAllByPatientId(pid)
            }
        }
    }

    override suspend fun getVisitByVid(vid: Long, requester: MediqToken): CustomResult<Visit, OrmFailureReason> {
        return if (requester can (Crud.READ to Tables.Visit)) {
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
                // checks that the doctor actually exists otherwise the `getAllByDoctorId`
                // will return an empty list if the doctor does not exist
                doctorRepository.getOne(did)
            } catch (failure: JpaObjectRetrievalFailureException) {
                // also note that the javadoc for getOne LIE! if the entity does not exist,
                // it throws a JpaObjectRetrievalFailureException wrapping a EntityNotFoundException
                // as opposed to whats documented which is an unwrapped EntityNotFoundException (thus the cause check)
                if (failure.cause is EntityNotFoundException) {
                    return Failure(DoesNotExist("doctor with id: $did not found"))
                } else {
                    throw failure
                }
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
            val event = eventRepository.getOne(visitInput.event.toLong())

            Success(visitRepository.save(Visit(visitInput, event)))
        } else {
            Failure(NotAuthorized(requester))
        }
    }

    override suspend fun getByExample(
        example: GraphQLVisitExample,
        strict: Boolean,
        requester: MediqToken
    ): CustomResult<List<Visit>, OrmFailureReason> {
        return if (requester can (Crud.READ to Tables.Patient)) {
            searchByGqlExample(entityManager, example, strict)
        } else {
            Failure(NotAuthorized(requester, "cannot read to patients"))
        }
    }
}