package com.leftindust.mockingbird.extensions

import com.expediagroup.graphql.generator.scalars.ID
import org.springframework.data.jpa.repository.JpaRepository
import javax.persistence.EntityNotFoundException

inline fun <reified T> JpaRepository<T, Long>.getByIds(ids: Collection<ID>): Set<T> {
    val result = findAllById(ids.map { it.toLong() }).toSet()
    if (result.size != ids.size)
        throw EntityNotFoundException("one or more of the requested ids was not found")
    else
        return result
}