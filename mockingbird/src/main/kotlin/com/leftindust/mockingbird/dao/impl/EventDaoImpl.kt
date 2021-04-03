package com.leftindust.mockingbird.dao.impl

import com.leftindust.mockingbird.auth.Authorizer
import com.leftindust.mockingbird.auth.Crud
import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.dao.EventDao
import com.leftindust.mockingbird.dao.NotAuthorized
import com.leftindust.mockingbird.dao.OrmFailureReason
import com.leftindust.mockingbird.dao.Tables
import com.leftindust.mockingbird.dao.entity.Event
import com.leftindust.mockingbird.dao.impl.repository.HibernateEventRepository
import com.leftindust.mockingbird.extensions.CustomResult
import com.leftindust.mockingbird.extensions.Failure
import com.leftindust.mockingbird.extensions.Success
import com.leftindust.mockingbird.graphql.types.input.GraphQLEventInput
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional
class EventDaoImpl(
    @Autowired private val hibernateEventRepository: HibernateEventRepository,
    @Autowired authorizer: Authorizer
) : EventDao, AbstractHibernateDao(authorizer) {
    override suspend fun addEvent(
        event: GraphQLEventInput,
        requester: MediqToken
    ): CustomResult<Event, OrmFailureReason> {
        return if (requester can (Crud.CREATE to Tables.Event)) {
            val eventEntity = Event(event)
            Success(hibernateEventRepository.save(eventEntity))
        } else {
            Failure(NotAuthorized(requester, "cannot create an event"))
        }
    }
}