package com.leftindust.mockingbird.extensions

import org.apache.logging.log4j.LogManager
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException
import javax.persistence.EntityNotFoundException

inline fun <reified T, ID> JpaRepository<T, ID>.getOneOrNull(id: ID): T? {
    return try {
        this.getOne(id)
    } catch (failure: JpaObjectRetrievalFailureException) {
        if (failure.cause is EntityNotFoundException) {
            LogManager.getLogger().error("failed to find entity ${T::class.simpleName}, with id $id")
            null
        } else {
            throw failure
        }
    }
}