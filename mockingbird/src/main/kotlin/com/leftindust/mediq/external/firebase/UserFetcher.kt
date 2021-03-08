package com.leftindust.mediq.external.firebase

import com.google.firebase.auth.ExportedUserRecord
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.UserRecord
import com.leftindust.mediq.auth.Authorizer
import com.leftindust.mediq.auth.Crud
import com.leftindust.mediq.auth.MediqToken
import com.leftindust.mediq.dao.DoesNotExist
import com.leftindust.mediq.dao.NotAuthorized
import com.leftindust.mediq.dao.OrmFailureReason
import com.leftindust.mediq.dao.Tables
import com.leftindust.mediq.dao.entity.Action
import com.leftindust.mediq.extensions.*
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
        if (authorizer.getAuthorization(readUsers, requester) == Authorization.Allowed) {
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