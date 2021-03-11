package com.leftindust.mediq.extensions

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException
import javax.persistence.EntityNotFoundException

fun <T, ID> JpaRepository<T, ID>.getOneOrNull(id: ID): T? {
    return try {
        this.getOne(id)
    } catch (failure: JpaObjectRetrievalFailureException) {
        if (failure.cause is EntityNotFoundException) {
            null
        } else {
            throw failure
        }
    }
}