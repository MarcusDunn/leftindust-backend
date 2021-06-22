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
import com.leftindust.mockingbird.graphql.types.GraphQLEvent
import com.leftindust.mockingbird.graphql.types.GraphQLPatient
import com.leftindust.mockingbird.graphql.types.GraphQLVisit
import com.leftindust.mockingbird.graphql.types.input.GraphQLVisitEditInput
import com.leftindust.mockingbird.graphql.types.input.GraphQLVisitInput
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.hibernate.SessionFactory
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager

@Repository
@Transactional
class VisitDaoImpl(
    authorizer: Authorizer,
    private val eventRepository: HibernateEventRepository,
    private val visitRepository: HibernateVisitRepository,
    private val sessionFactory: SessionFactory,
    private val patientRepository: HibernatePatientRepository,
) : VisitDao, AbstractHibernateDao(authorizer) {
    private val logger: Logger = LogManager.getLogger()

    override suspend fun getVisitByVid(vid: GraphQLVisit.ID, requester: MediqToken): Visit {
        return if (requester can (Crud.READ to Tables.Visit)) {
            visitRepository.getById(vid.id)
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
            val event = eventRepository.getById(visitInput.eid.id)
            visitRepository.save(Visit(visitInput, event))
        } else {
            throw NotAuthorizedException(requester, *requiredPermissions.toTypedArray())
        }
    }

    override suspend fun findByEvent(eid: GraphQLEvent.ID, requester: MediqToken): Visit? {
        return if (requester can (Crud.READ to Tables.Event)) {
            visitRepository.findByEvent_Id(eid.id)
        } else {
            throw NotAuthorizedException(requester, Crud.READ to Tables.Event)
        }
    }

    override suspend fun getByPatient(pid: GraphQLPatient.ID, requester: MediqToken): List<Visit> {
        return if (requester can listOf(Crud.READ to Tables.Event, Crud.READ to Tables.Visit)) {
            patientRepository.getById(pid.id).events.mapNotNull { visitRepository.findByEvent_Id(it.id!!) }
        } else {
            throw NotAuthorizedException(requester, Crud.READ to Tables.Event)
        }
    }

    override suspend fun editVisit(visit: GraphQLVisitEditInput, requester: MediqToken): Visit {
        val editVisit = Crud.UPDATE to Tables.Visit
        return if (requester can editVisit) {
            val visitEntity = visitRepository.getById(visit.vid.id)
            visitEntity.setByGqlInput(visit, sessionFactory.currentSession)
            visitEntity
        } else {
            throw NotAuthorizedException(requester, editVisit)
        }
    }
}