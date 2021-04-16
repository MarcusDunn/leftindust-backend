package com.leftindust.mockingbird.dao.impl

import com.expediagroup.graphql.generator.scalars.ID
import com.leftindust.mockingbird.auth.Authorizer
import com.leftindust.mockingbird.auth.Crud
import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.auth.NotAuthorizedException
import com.leftindust.mockingbird.dao.*
import com.leftindust.mockingbird.dao.entity.Event
import com.leftindust.mockingbird.dao.impl.repository.HibernateDoctorRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernateEventRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernatePatientRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernateVisitRepository
import com.leftindust.mockingbird.extensions.*
import com.leftindust.mockingbird.graphql.types.input.GraphQLEventEditInput
import com.leftindust.mockingbird.graphql.types.input.GraphQLEventInput
import com.leftindust.mockingbird.graphql.types.input.GraphQLTimeRangeInput
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional
class EventDaoImpl(
    @Autowired private val hibernateEventRepository: HibernateEventRepository,
    @Autowired private val hibernatePatientRepository: HibernatePatientRepository,
    @Autowired private val hibernateDoctorRepository: HibernateDoctorRepository,
    @Autowired private val hibernateVisitRepository: HibernateVisitRepository,
    @Autowired authorizer: Authorizer
) : EventDao, AbstractHibernateDao(authorizer) {
    override suspend fun addEvent(
        event: GraphQLEventInput,
        requester: MediqToken
    ): CustomResult<Event, OrmFailureReason> {
        return if (requester can (Crud.CREATE to Tables.Event)) {
            val patients = event.patients
                ?.map { hibernatePatientRepository.getOne(it.toLong()) }
                ?.toSet()
                ?: emptySet()
            val doctors = event.doctors
                ?.map { hibernateDoctorRepository.getOne(it.toLong()) }
                ?.toSet()
                ?: emptySet()
            val eventEntity = Event(event, doctors, patients)
            return Success(hibernateEventRepository.save(eventEntity))
        } else {
            Failure(NotAuthorized(requester, "cannot create an event"))
        }
    }

    override suspend fun getMany(
        range: GraphQLTimeRangeInput,
        requester: MediqToken
    ): Collection<Event> {
        if (requester can (Crud.READ to Tables.Event)) {
            return hibernateEventRepository.findAllByStartTimeAfterOrReoccurrenceIsNotNull(range.start.toTimestamp())
        } else {
            throw NotAuthorizedException(requester, Crud.READ to Tables.Event)
        }
    }

    override suspend fun getById(eid: ID, requester: MediqToken): CustomResult<Event, OrmFailureReason> {
        return if (requester can (Crud.READ to Tables.Event)) {
            val event = hibernateEventRepository.getOneOrNull(eid.toLong())
                ?: return Failure(DoesNotExist())
            Success(event)
        } else {
            Failure(NotAuthorized(requester, "cannot read events"))
        }
    }

    override suspend fun getByPatient(pid: Long, requester: MediqToken): Collection<Event> {
        if (requester can listOf(Crud.READ to Tables.Patient, Crud.READ to Tables.Event)) {
            return hibernatePatientRepository.getOne(pid).schedule.events
        } else {
            throw NotAuthorizedException(requester, Crud.READ to Tables.Patient, Crud.READ to Tables.Event)
        }
    }

    override suspend fun getByDoctor(did: Long, requester: MediqToken): Collection<Event> {
        if (requester can listOf(Crud.READ to Tables.Doctor, Crud.READ to Tables.Event)) {
            return hibernateDoctorRepository.getOne(did).schedule.events
        } else {
            throw NotAuthorizedException(requester, Crud.READ to Tables.Doctor, Crud.READ to Tables.Event)
        }
    }

    override suspend fun getByVisit(vid: Long, requester: MediqToken): Event {
        if (requester can listOf(Crud.READ to Tables.Visit, Crud.READ to Tables.Event)) {
            return hibernateVisitRepository.getOne(vid).event
        } else {
            throw NotAuthorizedException(requester, Crud.READ to Tables.Doctor, Crud.READ to Tables.Event)
        }
    }

    override suspend fun editEvent(event: GraphQLEventEditInput, requester: MediqToken): Event {
        TODO("Not yet implemented")
    }
}