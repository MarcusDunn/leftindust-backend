package com.leftindust.mockingbird.graphql.queries

import com.expediagroup.graphql.exceptions.GraphQLKotlinException
import com.expediagroup.graphql.scalars.ID
import com.leftindust.mockingbird.dao.entity.MediqUser
import com.leftindust.mockingbird.dao.entity.UserSettings
import com.leftindust.mockingbird.extensions.CustomResultException
import com.leftindust.mockingbird.graphql.types.GraphQLUser
import com.leftindust.mockingbird.graphql.types.input.GraphQLRangeInput
import com.leftindust.mockingbird.helper.FakeAuth
import kotlinx.coroutines.runBlocking
import org.hibernate.SessionFactory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
internal class UserQueryTest(
    @Autowired private val userQuery: UserQuery,
) {


    @Autowired
    private lateinit var sessionFactory: SessionFactory

    private val session
        get() = sessionFactory.currentSession

    @Test
    fun user() {
        val user = MediqUser(
            uniqueId = "TEST USER",
            settings = UserSettings(1)
        )
        session.save(user)

        val result = runBlocking { userQuery.user(ID(user.uniqueId), FakeAuth.Valid.Context) }

        assertEquals(result, GraphQLUser(user, FakeAuth.Valid.Context))
    }

    @Test
    fun `user without valid user`() {
        val exception = assertThrows(CustomResultException::class.java) {
            runBlocking { userQuery.user(ID("DOES NOT EXIST"), FakeAuth.Valid.Context) }
        }

        assert(exception.message!!.contains("DoesNotExist"))
    }

    @Test
    fun users() {
        val user = MediqUser(
            uniqueId = "TEST USER",
            settings = UserSettings(1)
        ).also { session.save(it) }
        val user1 = MediqUser(
            uniqueId = "TEST USER1",
            settings = UserSettings(1)
        ).also { session.save(it) }

        val result: List<GraphQLUser> = runBlocking { userQuery.users(graphQLAuthContext = FakeAuth.Valid.Context) }

        val expectedUser = GraphQLUser(user, FakeAuth.Valid.Context)
        val expectedUser1 = GraphQLUser(user1, FakeAuth.Valid.Context)

        assert(result.containsAll(listOf(expectedUser, expectedUser1)))
    }

    @Test
    fun usersByUids() {
        val user = MediqUser(
            uniqueId = "TEST USER",
            settings = UserSettings(1)
        ).also { session.save(it) }
        val user1 = MediqUser(
            uniqueId = "TEST USER1",
            settings = UserSettings(1)
        ).also { session.save(it) }

        val result: List<GraphQLUser> = runBlocking {
            userQuery.users(
                uniqueIds = listOf(user.uniqueId, user1.uniqueId).map { ID(it) },
                graphQLAuthContext = FakeAuth.Valid.Context
            )
        }

        assertEquals(result.toSet(), listOf(user, user1).map { GraphQLUser(it, FakeAuth.Valid.Context) }.toSet())
    }

    /*
    This test relies on internal firebase projects at leftindust it will fail on non-leftindust projects
     */
    @Test
    @Tag("firebase")
    fun firebaseUsers() {
        val result = runBlocking {
            userQuery.firebaseUsers(
                GraphQLRangeInput(
                    from = 0,
                    to = 1
                ),
                graphQLAuthContext = FakeAuth.Valid.Context
            )
        }

        assertEquals(1, result.size) { result.toString() }
    }

    /*
    This test relies on internal firebase projects at leftindust it will fail on non-leftindust projects
     */
    @Test
    @Tag("firebase")
    fun `firebaseUsers too many`() {
        val result = runBlocking {
            userQuery.firebaseUsers(
                GraphQLRangeInput(
                    from = 0,
                    to = 10000
                ),
                graphQLAuthContext = FakeAuth.Valid.Context
            )
        }

        assert(10000 >= result.size)
    }

    @Test
    @Tag("firebase")
    fun `firebaseUsers invalid request`() {

        val result = assertThrows(GraphQLKotlinException::class.java) {
            runBlocking {
                userQuery.firebaseUsers(
                    GraphQLRangeInput(
                        from = 0,
                        to = 0
                    ),
                    graphQLAuthContext = FakeAuth.Valid.Context
                )
            }
        }

        assert(result.message!!.contains("invalid")) { result }
    }

    @Test
    @Tag("firebase")
    fun `firebaseUsers filter`() {

        val result = runBlocking {
            userQuery.firebaseUsers(
                GraphQLRangeInput(
                    from = 0,
                    to = 100
                ),
                filterRegistered = true,
                graphQLAuthContext = FakeAuth.Valid.Context
            )
        }

        assert(result.size <= 100) { result }
    }
}