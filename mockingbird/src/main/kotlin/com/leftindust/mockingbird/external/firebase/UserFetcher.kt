package com.leftindust.mockingbird.external.firebase

import com.google.firebase.auth.ExportedUserRecord
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.UserRecord
import com.leftindust.mockingbird.auth.Authorizer
import com.leftindust.mockingbird.auth.Crud
import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.dao.DoesNotExist
import com.leftindust.mockingbird.dao.NotAuthorized
import com.leftindust.mockingbird.dao.OrmFailureReason
import com.leftindust.mockingbird.dao.Tables
import com.leftindust.mockingbird.dao.entity.Action
import com.leftindust.mockingbird.extensions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class UserFetcher(
    @Autowired private val authorizer: Authorizer,
    @Autowired private val firebaseAuth: FirebaseAuth
) {

    suspend fun getUserInfo(
        uid: String,
        requester: MediqToken
    ): CustomResult<UserRecord, OrmFailureReason> {
        val readUsers = Action(Crud.READ to Tables.User)
        if ((requester.uid == uid && requester.isVerified()) || authorizer.getAuthorization(readUsers, requester) == Authorization.Allowed) {
            val user = try {
                firebaseAuth.getUser(uid)
            } catch (e: FirebaseAuthException) {
                return Failure(DoesNotExist())
            }
            return Success(user ?: return Failure(DoesNotExist()))
        } else {
            return Failure(NotAuthorized(requester))
        }
    }

    suspend fun getUsers(requester: MediqToken): CustomResult<MutableIterable<ExportedUserRecord>, OrmFailureReason> {
        return if (authorizer.getAuthorization(Action(Crud.READ to Tables.User), requester).isAllowed()) {
            Success(firebaseAuth.listUsers(null).iterateAll() ?: return Failure(DoesNotExist("list users has returned null")))
        } else {
            Failure(NotAuthorized(requester, "cannot read bulk users"))
        }
    }
}