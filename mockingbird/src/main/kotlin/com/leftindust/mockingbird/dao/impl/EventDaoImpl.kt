package com.leftindust.mockingbird.dao.impl

import com.expediagroup.graphql.generator.scalars.ID
import com.leftindust.mockingbird.auth.Authorizer
import com.leftindust.mockingbird.auth.Crud
import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.dao.*
import com.leftindust.mockingbird.dao.entity.Event
import com.leftindust.mockingbird.dao.impl.repository.HibernateDoctorRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernateEventRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernatePatientRepository
import com.leftindust.mockingbird.extensions.*
import com.leftindust.mockingbird.graphql.types.input.GraphQLEventInput
import com.leftindust.mockingbird.graphql.types.input.GraphQLRangeInput
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional
class EventDaoImpl(
    @Autowired private val hibernateEventRepository: HibernateEventRepository,
    @Autowired private val hibernatePatientRepository: HibernatePatientRepository,
    @Autowired private val hibernateDoctorRepository: HibernateDoctorRepository,
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
            return Success(eventEntity)
        } else {
            Failure(NotAuthorized(requester, "cannot create an event"))
        }
    }

    override suspend fun getMany(
        range: GraphQLRangeInput,
        requester: MediqToken
    ): CustomResult<List<Event>, OrmFailureReason> {
        return if (requester can (Crud.READ to Tables.Event)) {
            val size = range.to - range.from
            val page = range.to / size - 1
            return Success(hibernateEventRepository.findAll(PageRequest.of(page, size)).toList())
        } else {
            Failure(NotAuthorized(requester, "cannot read events"))

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
}