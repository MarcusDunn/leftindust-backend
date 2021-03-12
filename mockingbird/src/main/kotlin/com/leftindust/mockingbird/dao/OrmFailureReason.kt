package com.leftindust.mockingbird.dao

import com.leftindust.mockingbird.auth.MediqToken

/**
 * Enumeration type over possible reasons for failure within the ORM
 */
sealed class OrmFailureReason

data class InvalidArguments(val details: String? = null) : OrmFailureReason()
data class AlreadyExists(val details: String? = null) : OrmFailureReason()
data class DoesNotExist(val details: String? = null) : OrmFailureReason()
data class NotAuthorized(val token: MediqToken, val details: String? = null) : OrmFailureReason()
data class NoUidForUser(val details: String? = null) : OrmFailureReason()

