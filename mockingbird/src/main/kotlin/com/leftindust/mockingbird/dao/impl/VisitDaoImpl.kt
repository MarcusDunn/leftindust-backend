package com.leftindust.mockingbird.dao.impl

import com.leftindust.mockingbird.auth.Authorizer
import com.leftindust.mockingbird.auth.Crud
import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.auth.NotAuthorizedException
import com.leftindust.mockingbird.dao.Tables
import com.leftindust.mockingbird.dao.VisitDao
import com.leftindust.mockingbird.dao.entity.Visit
import com.leftindust.mockingbird.dao.impl.repository.HibernateEventRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernatePatientRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernateVisitRepository
import com.leftindust.mockingbird.extensions.toLong
import com.leftindust.mockingbird.graphql.types.input.GraphQLVisitInput
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager

@Repository
@Transactional
class VisitDaoImpl(
    authorizer: Authorizer,
    private val eventRepository: HibernateEventRepository,
    private val visitRepository: HibernateVisitRepository,
    private val entityManager: EntityManager,
    private val patientRepository: HibernatePatientRepository,
) : VisitDao, AbstractHibernateDao(authorizer) {
    private val logger: Logger = LogManager.getLogger()

    override suspend fun getVisitByVid(vid: Long, requester: MediqToken): Visit {
        return if (requester can (Crud.READ to Tables.Visit)) {
            visitRepository.getOne(vid)
        } else {
            throw NotAuthorizedException(requester, Crud.READ to Tables.Visit)
        }
    }

    override suspend fun addVisit(
        visitInput: GraphQLVisitInput,
        requester: MediqToken
    ): Visit {
        val requiredPermissions = listOf(
            Crud.READ to Tables.Doctor,
            Crud.READ to Tables.Patient,
            Crud.CREATE to Tables.Visit,
        )

        return if (requester can requiredPermissions) {
            val event = eventRepository.getOne(visitInput.event.toLong())
            visitRepository.save(Visit(visitInput, event))
        } else {
            throw NotAuthorizedException(requester, *requiredPermissions.toTypedArray())
        }
    }

    override suspend fun getByEvent(id: Long, requester: MediqToken): Visit {
        return if (requester can (Crud.READ to Tables.Event)) {
            visitRepository.getByEvent_Id(id)
        } else {
            throw NotAuthorizedException(requester, Crud.READ to Tables.Event)
        }
    }

    override suspend fun getByPatient(pid: Long, requester: MediqToken): List<Visit> {
        return if (requester can listOf(Crud.READ to Tables.Event, Crud.READ to Tables.Visit)) {
            patientRepository.getOne(pid).schedule.events.map { visitRepository.getByEvent_Id(it.id!!) }
        } else {
            throw NotAuthorizedException(requester, Crud.READ to Tables.Event)
        }
    }
}