package com.leftindust.mockingbird.dao.impl

import com.leftindust.mockingbird.auth.Authorizer
import com.leftindust.mockingbird.auth.Crud
import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.auth.NotAuthorizedException
import com.leftindust.mockingbird.dao.*
import com.leftindust.mockingbird.dao.entity.Action
import com.leftindust.mockingbird.dao.entity.Visit
import com.leftindust.mockingbird.dao.impl.repository.HibernateEventRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernateVisitRepository
import com.leftindust.mockingbird.extensions.CustomResult
import com.leftindust.mockingbird.extensions.Failure
import com.leftindust.mockingbird.extensions.Success
import com.leftindust.mockingbird.extensions.toLong
import com.leftindust.mockingbird.graphql.types.GraphQLVisitInput
import com.leftindust.mockingbird.graphql.types.examples.GraphQLVisitExample
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException
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
) : VisitDao, AbstractHibernateDao(authorizer) {
    private val logger: Logger = LogManager.getLogger()

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

    override suspend fun getByEvent(id: Long, requester: MediqToken): Visit {
        return if (requester can (Crud.READ to Tables.Event)) {
            visitRepository.getByEvent_Id(id)
        } else {
            throw NotAuthorizedException(requester, Crud.READ to Tables.Event)
        }
    }
}