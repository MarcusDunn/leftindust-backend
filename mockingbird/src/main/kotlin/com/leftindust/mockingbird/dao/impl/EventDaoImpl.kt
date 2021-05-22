package com.leftindust.mockingbird.dao.impl

import com.expediagroup.graphql.generator.scalars.ID
import com.leftindust.mockingbird.auth.Authorizer
import com.leftindust.mockingbird.auth.Crud
import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.auth.NotAuthorizedException
import com.leftindust.mockingbird.dao.EventDao
import com.leftindust.mockingbird.dao.Tables
import com.leftindust.mockingbird.dao.entity.Event
import com.leftindust.mockingbird.dao.entity.Reoccurrence
import com.leftindust.mockingbird.dao.impl.repository.*
import com.leftindust.mockingbird.extensions.getByIds
import com.leftindust.mockingbird.extensions.toLong
import com.leftindust.mockingbird.graphql.types.input.GraphQLEventEditInput
import com.leftindust.mockingbird.graphql.types.input.GraphQLEventInput
import com.leftindust.mockingbird.graphql.types.input.GraphQLRecurrenceEditSettings
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
    ): Event {
        return if (requester can (Crud.CREATE to Tables.Event)) {
            val patients = event.patients?.let { hibernatePatientRepository.getByIds(it) } ?: emptySet()
            val doctors = event.doctors?.let { hibernateDoctorRepository.getByIds(it) } ?: emptySet()
            val eventEntity = Event(event, doctors, patients)
            hibernateEventRepository.save(eventEntity)
        } else {
            throw NotAuthorizedException(requester, Crud.CREATE to Tables.Event)
        }
    }

    override suspend fun getMany(
        range: GraphQLTimeRangeInput,
        requester: MediqToken
    ): Collection<Event> {
        if (requester can (Crud.READ to Tables.Event)) {
            return hibernateEventRepository.getAllMatchingOrHasRecurrence(range.start.toTimestamp())
        } else {
            throw NotAuthorizedException(requester, Crud.READ to Tables.Event)
        }
    }

    override suspend fun getById(eid: ID, requester: MediqToken): Event {
        return if (requester can (Crud.READ to Tables.Event)) {
            hibernateEventRepository.getOne(eid.toLong())
        } else {
            throw NotAuthorizedException(requester, Crud.READ to Tables.Event)
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

    override suspend fun editEvent(
        event: GraphQLEventEditInput,
        requester: MediqToken
    ): Event {
        if (requester can (Crud.UPDATE to Tables.Event)) {
            val entity = hibernateEventRepository.getOne(event.eid.toLong())

            if (entity.reoccurrence != null) {
                throw IllegalArgumentException("cannot call editEvent on a recurring event")
            }

            val doctors = event.doctors?.let { hibernateDoctorRepository.getByIds(it) }
            val patients = event.patients?.let { hibernatePatientRepository.getByIds(it) }
            val updatedEntity = entity.update(event, newDoctors = doctors, newPatients = patients)

            // prevents double references to collections between updatedEntity and entity
            hibernateEventRepository.delete(entity)

            return hibernateEventRepository.save(updatedEntity)
        } else {
            throw NotAuthorizedException(requester, Crud.UPDATE to Tables.Event)
        }
    }

    // this function has some issues. but basically does as follows
    // in the simplest case, if the recurrence settings cover the entirety of the event recurrence, we simply replace
    // the event with a single edited event
    // if the user only wants to edit part of the recurrence period, we create up to 3 events.
    // one for the edited event within the recurrenceSettings time period
    // one for the time before recurrenceSettings.editStart, that is unmodified except for that the recurrence now ends when the edited event begins
    // and finally one for after the the edited period, this is also unchanged.
    // keep in mind that recurrenceSettings can potentially cover only the tail or start of the event, in which case we end up with only 2 events
    override suspend fun editRecurringEvent(
        event: GraphQLEventEditInput,
        requester: MediqToken,
        recurrenceSettings: GraphQLRecurrenceEditSettings
    ): Event {
        if (requester can (Crud.UPDATE to Tables.Event)) {
            val entity = hibernateEventRepository.getOne(event.eid.toLong())

            if (entity.reoccurrence == null) {
                throw IllegalArgumentException("cannot call editRecurringEvent on a non-recurring event")
            }

            val currentStartDate = entity.reoccurrence!!.startDate

            val currentEndDate = entity.reoccurrence!!.endDate

            hibernateEventRepository.delete(entity)


            if (currentEndDate.isAfter(recurrenceSettings.editEnd.toLocalDate())) { // there will be a trailing unedited event
                val trailingEntity = entity.clone().apply {
                    id = null
                    reoccurrence = Reoccurrence(
                        // toMutableList clones the collection to avoid shared references to a collection
                        days = reoccurrence!!.days.toMutableList(),
                        startDate = reoccurrence!!.startDate,
                        endDate = recurrenceSettings.editStart.toLocalDate()
                    )
                }
                hibernateEventRepository.save(trailingEntity)
            }

            if (currentStartDate.isBefore(recurrenceSettings.editStart.toLocalDate())) { // there will be a unedited event prior to the edited one
                val priorEntity = entity.clone().apply {
                    id = null
                    reoccurrence = Reoccurrence(
                        // toMutableList clones the collection to avoid shared references to a collection
                        days = reoccurrence!!.days.toMutableList(),
                        startDate = recurrenceSettings.editEnd.toLocalDate(),
                        endDate = reoccurrence!!.endDate
                    )
                }
                hibernateEventRepository.save(priorEntity)
            }

            val doctors = event.doctors?.let { hibernateDoctorRepository.getByIds(it) }
            val patients = event.patients?.let { hibernatePatientRepository.getByIds(it) }
            val updatedEntity = entity.clone().apply { // we keep the same id
                reoccurrence = Reoccurrence(
                    startDate = recurrenceSettings.editStart.toLocalDate(),
                    endDate = recurrenceSettings.editEnd.toLocalDate(),
                    // toMutableList clones the collection to avoid shared references to a collection
                    days = reoccurrence!!.days.toMutableList()
                )
            }.update(event, newDoctors = doctors, newPatients = patients)

            return hibernateEventRepository.save(updatedEntity)

        } else {
            throw NotAuthorizedException(requester, Crud.UPDATE to Tables.Event)
        }
    }

    override suspend fun getBetween(range: GraphQLTimeRangeInput, requester: MediqToken): List<Event> {
        val readEvents = Crud.READ to Tables.Event
        return if (requester can readEvents) {
            hibernateEventRepository.findAllByStartTimeBeforeAndEndTimeAfterOrReoccurrenceIsNotNull(range.start.toTimestamp(), range.end.toTimestamp())
        } else {
            throw NotAuthorizedException(requester, readEvents)
        }
    }
}