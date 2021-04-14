package integration.graphql.mutations

import com.leftindust.mockingbird.MockingbirdApplication
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.impl.repository.HibernateEventRepository
import com.leftindust.mockingbird.graphql.mutations.EventMutation
import com.leftindust.mockingbird.graphql.types.*
import com.leftindust.mockingbird.graphql.types.input.GraphQLEventInput
import integration.util.EntityStore
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
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
            eid = null,
            title = "EventMutationTest.add event with recurrence",
            description = "some description",
            start = GraphQLTimeInput(Timestamp.valueOf("2020-01-02 09:00:00")),
            end = GraphQLTimeInput(Timestamp.valueOf("2020-01-02 10:00:00")),
            allDay = false,
            doctors = emptyList(),
            patients = emptyList(),
            reoccurrence = GraphQLRecurrence(
                startDate = GraphQLDate(2, GraphQLMonth.Jan, 2020),
                endDate = GraphQLDate(22, GraphQLMonth.Jan, 2020),
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
}