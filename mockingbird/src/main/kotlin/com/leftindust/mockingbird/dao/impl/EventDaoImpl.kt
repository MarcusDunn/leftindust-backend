package com.leftindust.mockingbird.dao.impl

import com.leftindust.mockingbird.auth.Authorizer
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.EventDao
import com.leftindust.mockingbird.dao.OrmFailureReason
import com.leftindust.mockingbird.dao.entity.Event
import com.leftindust.mockingbird.dao.impl.repository.HibernateEventRepository
import com.leftindust.mockingbird.extensions.CustomResult
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
    override fun addEvent(
        event: GraphQLEventInput,
        graphQLAuthContext: GraphQLAuthContext
    ): CustomResult<Event, OrmFailureReason> {
        TODO("Not yet implemented")
    }
}