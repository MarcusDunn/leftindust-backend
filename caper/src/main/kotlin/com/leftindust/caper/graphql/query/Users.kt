package com.leftindust.caper.graphql.query

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.exceptions.GraphQLKotlinException
import com.expediagroup.graphql.generator.execution.OptionalInput
import com.expediagroup.graphql.types.operations.Query
import com.leftindust.caper.extensions.getOrDefault
import com.leftindust.caper.graphql.GqlAuthContext
import com.leftindust.caper.sql.tables.MediqUser
import com.leftindust.caper.sql.tables.MediqUsers
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.springframework.stereotype.Component

@Component
class Users : Query {
    @GraphQLDescription("gets a user by uid, defaults to the uid of the mediq-auth-token header")
    fun user(uid: OptionalInput<String>, gqlAuthContext: GqlAuthContext): GqlUser? {
        return transaction {
            MediqUser
                .find { MediqUsers.uid eq uid.getOrDefault(gqlAuthContext.mediqAuthToken.uid) }
                .singleOrNull()
        }?.toGql()
    }

    @GraphQLDescription("adds a new user")
    fun addUser(user: AddUserInput, gqlAuthContext: GqlAuthContext): GqlUser? {
        return transaction {
            MediqUser.new {
                uid = user.uid
                settingsJson = user.settingsJson
                settingsVersion = user.settingsVersion
            }
        }.toGql()
    }

    @GraphQLDescription("updates a user by uid")
    fun updateUser(user: UpdateUserInput, gqlAuthContext: GqlAuthContext): Int {
        return transaction {
            MediqUsers.update({ MediqUsers.uid eq user.uid }) {
                if (user.settingsJson is OptionalInput.Defined) {
                    it[settingsJson] = user.settingsJson.value!!
                }
                if (user.settingsVersion is OptionalInput.Defined)
                    it[settingsVersion] = user.settingsVersion.value!!
            }
        }
    }

    @GraphQLDescription("deletes a user by uid")
    fun deleteUser(uid: String, gqlAuthContext: GqlAuthContext): Int {
        if (uid == gqlAuthContext.mediqAuthToken.uid) throw GraphQLKotlinException("the user cannot have the same uid as the uid of mediq-auth-token when deleting")
        return transaction {
            MediqUsers.deleteWhere {
                MediqUsers.uid eq uid
            }
        }
    }

    @GraphQLDescription("a user")
    data class GqlUser(
        val uid: String,
        val settingsJson: String,
        val settingsVersion: Int,
    )

    @GraphQLDescription("input for adding users")
    data class AddUserInput(
        val uid: String,
        val settingsJson: String,
        val settingsVersion: Int,
    )

    @GraphQLDescription("input for updating users, not setting fields will not update them, setting them to null will")
    data class UpdateUserInput(
        val uid: String,
        val settingsJson: OptionalInput<String>,
        val settingsVersion: OptionalInput<Int>,
    )
}
