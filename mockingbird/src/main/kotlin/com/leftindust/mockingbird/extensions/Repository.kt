package com.leftindust.mockingbird.extensions

import com.expediagroup.graphql.generator.scalars.ID
import org.apache.logging.log4j.LogManager
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException
import javax.persistence.EntityNotFoundException


inline fun <reified T> JpaRepository<T, Long>.getByIds(ids: Collection<ID>): Set<T> =
    ids
        .map { this.getOne(it.toLong()) }
        .toSet()