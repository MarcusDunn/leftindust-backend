package integration.graphql.mutations

import com.expediagroup.graphql.generator.execution.OptionalInput
import com.leftindust.mockingbird.MockingbirdApplication
import com.leftindust.mockingbird.auth.Authorizer
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.dao.impl.repository.HibernateEventRepository
import com.leftindust.mockingbird.extensions.Authorization
import com.leftindust.mockingbird.graphql.mutations.EventMutation
import com.leftindust.mockingbird.graphql.types.GraphQLDayOfWeek
import com.leftindust.mockingbird.graphql.types.GraphQLMonth
import com.leftindust.mockingbird.graphql.types.GraphQLUtcTime
import com.leftindust.mockingbird.graphql.types.input.*
import com.ninjasquad.springmockk.MockkBean
import integration.util.EntityStore
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.transaction.annotation.Transactional
import java.sql.Timestamp

@SpringBootTest(classes = [MockingbirdApplication::class])
@Tag("Integration")
@Transactional
class EventMutationTest(
    @Autowired private val eventMutation: EventMutation,
    @Autowired private val hibernateEventRepository: HibernateEventRepository
) {
    @MockkBean
    private lateinit var authorizer: Authorizer

    @Test
    internal fun `add event no recurrence`() {
        val event = EntityStore.graphQLEventInput("EventMutationTest.add event no recurrence")

        val mediqToken = mockk<MediqToken>()
        val graphQLAuthContext = mockk<GraphQLAuthContext> {
            every { mediqAuthToken } returns mediqToken
        }
        coEvery { authorizer.getAuthorization(any(), mediqToken) } returns Authorization.Allowed
        runBlocking { eventMutation.addEvent(event, graphQLAuthContext) }

        assertDoesNotThrow {
            hibernateEventRepository.getAllByTitleEquals("EventMutationTest.add event no recurrence")
        }
    }

    @Test
    internal fun `add event with recurrence`() {
        val event = GraphQLEventInput(
            title = "EventMutationTest.add event with recurrence",
            description = "some description",
            start = GraphQLUtcTime(Timestamp.valueOf("2020-01-02 09:00:00")),
            end = GraphQLUtcTime(Timestamp.valueOf("2020-01-02 10:00:00")),
            allDay = false,
            doctors = emptyList(),
            patients = emptyList(),
            recurrence = GraphQLRecurrenceInput(
                startDate = GraphQLDateInput(2, GraphQLMonth.Jan, 2020),
                endDate = GraphQLDateInput(22, GraphQLMonth.Jan, 2020),
                daysOfWeek = GraphQLDayOfWeek.values().toList() // every day
            ),
        )

        val mediqToken = mockk<MediqToken>()
        val graphQLAuthContext = mockk<GraphQLAuthContext> {
            every { mediqAuthToken } returns mediqToken
        }
        coEvery { authorizer.getAuthorization(any(), mediqToken) } returns Authorization.Allowed

        runBlocking { eventMutation.addEvent(event, graphQLAuthContext) }

        assertDoesNotThrow {
            hibernateEventRepository.getAllByTitleEquals("EventMutationTest.add event with recurrence")
        }
    }

    @Test
    internal fun `edit event`() {
        val mediqToken = mockk<MediqToken>()
        val graphQLAuthContext = mockk<GraphQLAuthContext> {
            every { mediqAuthToken } returns mediqToken
        }
        coEvery { authorizer.getAuthorization(any(), mediqToken) } returns Authorization.Allowed

        val eid = runBlocking {
            eventMutation.addEvent(
                EntityStore.graphQLEventInput("EventMutationTest.edit event"),
                graphQLAuthContext
            ).eid
        }

        val newDescription = "some fancy new description"

        val event = GraphQLEventEditInput(
            eid = eid,
            description = OptionalInput.Defined(newDescription),
        )

        val result = runBlocking {
            eventMutation.editEvent(event, graphQLAuthContext)
        }

        // check returned value is changed
        assertEquals(result.description, newDescription)

        val entity = assertDoesNotThrow {
            hibernateEventRepository.getAllByTitleEquals("EventMutationTest.edit event")
        }

        // check changes are persisted
        assertEquals(entity.first().description, newDescription)
    }

    @Test
    internal fun `edit event with recurrence`() {
        val mediqToken = mockk<MediqToken>()
        val graphQLAuthContext = mockk<GraphQLAuthContext> {
            every { mediqAuthToken } returns mediqToken
        }
        coEvery { authorizer.getAuthorization(any(), mediqToken) } returns Authorization.Allowed

        val recurrence = GraphQLRecurrenceInput(
            startDate = GraphQLDateInput(1, GraphQLMonth.Mar, 2020),
            endDate = GraphQLDateInput(1, GraphQLMonth.Mar, 2030),
            daysOfWeek = listOf(GraphQLDayOfWeek.Mon)
        )

        val originalWithRecurrence = EntityStore
            .graphQLEventInput("EventMutationTest.edit event with recurrence")
            .copy(recurrence = recurrence)

        val eid = runBlocking {
            eventMutation.addEvent(
                originalWithRecurrence,
                graphQLAuthContext
            ).eid
        }

        val newDescription = "some fancy new description"

        val event = GraphQLEventEditInput(
            eid = eid,
            description = OptionalInput.Defined(newDescription),
        )

        val recurrenceSettings = GraphQLRecurrenceEditSettings(
            editStart = GraphQLDateInput(1, GraphQLMonth.Mar, 2021),
            editEnd = GraphQLDateInput(1, GraphQLMonth.Mar, 2022),
        )

        val result = runBlocking {
            eventMutation.editRecurringEvent(event, graphQLAuthContext, recurrenceSettings)
        }

        // check returned value is changed
        assertEquals(result.description, newDescription)

        val entities = assertDoesNotThrow {
            hibernateEventRepository.getAllByTitleEquals("EventMutationTest.edit event with recurrence")
        }

        assertEquals(3, entities.size)
        with(entities.sortedBy { it.reoccurrence!!.startDate }) {
            // the original segment up until changes took place
            assertEquals(recurrence.startDate.toLocalDate(), get(0).reoccurrence!!.startDate)
            assertEquals(recurrenceSettings.editStart.toLocalDate(), get(0).reoccurrence!!.endDate)
            assertEquals(originalWithRecurrence.description, get(0).description)


            // the edited segment
            assertEquals(recurrenceSettings.editStart.toLocalDate(), get(1).reoccurrence!!.startDate)
            assertEquals(recurrenceSettings.editEnd.toLocalDate(), get(1).reoccurrence!!.endDate)
            assertEquals(newDescription, get(1).description)


            // the original segment after the edits end
            assertEquals(recurrenceSettings.editEnd.toLocalDate(), get(2).reoccurrence!!.startDate)
            assertEquals(recurrence.endDate.toLocalDate(), get(2).reoccurrence!!.endDate)
            assertEquals(originalWithRecurrence.description, get(2).description)
        }
    }
}