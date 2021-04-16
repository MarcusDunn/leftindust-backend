package integration.graphql.mutations

import com.expediagroup.graphql.generator.execution.OptionalInput
import com.leftindust.mockingbird.MockingbirdApplication
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.impl.repository.HibernateEventRepository
import com.leftindust.mockingbird.extensions.gqlID
import com.leftindust.mockingbird.graphql.mutations.EventMutation
import com.leftindust.mockingbird.graphql.types.GraphQLDayOfWeek
import com.leftindust.mockingbird.graphql.types.GraphQLMonth
import com.leftindust.mockingbird.graphql.types.GraphQLTimeInput
import com.leftindust.mockingbird.graphql.types.input.GraphQLDateInput
import com.leftindust.mockingbird.graphql.types.input.GraphQLEventEditInput
import com.leftindust.mockingbird.graphql.types.input.GraphQLEventInput
import com.leftindust.mockingbird.graphql.types.input.GraphQLRecurrenceInput
import integration.util.EntityStore
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import java.sql.Timestamp

@SpringBootTest(classes = [MockingbirdApplication::class])
@Tag("Integration")
@Transactional
class EventMutationTest(
    @Autowired private val eventMutation: EventMutation,
    @Autowired private val hibernateEventRepository: HibernateEventRepository
) {
    @Test
    internal fun `add event no recurrence`() {
        val event = EntityStore.graphQLEventInput("EventMutationTest.add event no recurrence")

        val graphQLAuthContext = mockk<GraphQLAuthContext> {
            every { mediqAuthToken } returns mockk() {
                every { uid } returns "admin"
            }
        }

        runBlocking { eventMutation.addEvent(event, graphQLAuthContext) }

        assertDoesNotThrow {
            hibernateEventRepository.findByTitleEquals("EventMutationTest.add event no recurrence")
        }
    }

    @Test
    internal fun `add event with recurrence`() {
        val event = GraphQLEventInput(
            title = "EventMutationTest.add event with recurrence",
            description = "some description",
            start = GraphQLTimeInput(Timestamp.valueOf("2020-01-02 09:00:00")),
            end = GraphQLTimeInput(Timestamp.valueOf("2020-01-02 10:00:00")),
            allDay = false,
            doctors = emptyList(),
            patients = emptyList(),
            recurrence = GraphQLRecurrenceInput(
                startDate = GraphQLDateInput(2, GraphQLMonth.Jan, 2020),
                endDate = GraphQLDateInput(22, GraphQLMonth.Jan, 2020),
                daysOfWeek = GraphQLDayOfWeek.values().toList() // every day
            ),
        )

        val graphQLAuthContext = mockk<GraphQLAuthContext> {
            every { mediqAuthToken } returns mockk() {
                every { uid } returns "admin"
            }
        }

        runBlocking { eventMutation.addEvent(event, graphQLAuthContext) }

        assertDoesNotThrow {
            hibernateEventRepository.findByTitleEquals("EventMutationTest.add event with recurrence")
        }
    }

    @Test
    internal fun `edit event`() {
        val graphQLAuthContext = mockk<GraphQLAuthContext> {
            every { mediqAuthToken } returns mockk() {
                every { uid } returns "admin"
            }
        }

        val eid = runBlocking {
            eventMutation.addEvent(
                EntityStore.graphQLEventInput("EventMutationTest.edit event"),
                graphQLAuthContext
            ).eid
        }

        val newDescription = "some fancy new description"

        val event = GraphQLEventEditInput(
            eid = gqlID(eid),
            description = OptionalInput.Defined(newDescription),
        )

        // check returned value is changed
        assertEquals(event.description, newDescription)

        val result = assertDoesNotThrow {
            hibernateEventRepository.findByTitleEquals("EventMutationTest.edit event")
        }

        // check changes are persisted
        assertEquals(result.description, newDescription)
    }
}