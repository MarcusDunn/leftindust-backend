package integration.dao.entity

import com.leftindust.mockingbird.MockingbirdApplication
import com.leftindust.mockingbird.auth.Authorizer
import com.leftindust.mockingbird.dao.VisitDao
import com.leftindust.mockingbird.dao.entity.Visit
import com.leftindust.mockingbird.extensions.Authorization
import com.leftindust.mockingbird.graphql.types.GraphQLEvent
import com.leftindust.mockingbird.graphql.types.icd.FoundationIcdCodeTest
import com.leftindust.mockingbird.graphql.types.icd.GraphQLFoundationIcdCode
import com.leftindust.mockingbird.graphql.types.icd.GraphQLFoundationIcdCodeInput
import com.leftindust.mockingbird.graphql.types.input.GraphQLVisitInput
import com.ninjasquad.springmockk.MockkBean
import integration.util.EntityStore
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.boot.test.context.SpringBootTest
import javax.transaction.Transactional

@SpringBootTest(classes = [MockingbirdApplication::class])
@Transactional
@Tag("Integration")
@AutoConfigureTestEntityManager
class VisitDaoTest(
    @Autowired private val testEntityManager: TestEntityManager,
    @Autowired private val visitDao: VisitDao,
) {
    @MockkBean
    private lateinit var authorizer: Authorizer

    @Test
    internal fun `add visit`() {
        coEvery { authorizer.getAuthorization(any(), any()) } returns Authorization.Allowed

        val patient = testEntityManager.persist(EntityStore.patient("VisitDaoTest.`add visit`"))
        val event = testEntityManager.persist(EntityStore.event("VisitDaoTest.`add visit`"))
        event.patients.add(patient)
        testEntityManager.flush()

        val visitInput = GraphQLVisitInput(
            eid = GraphQLEvent.ID(event.id!!),
            title = "Some visit",
            description = "Some description",
            foundationIcdCodes = listOf(GraphQLFoundationIcdCodeInput("some url!"), GraphQLFoundationIcdCodeInput("some other url!")),
        )

        val result = runBlocking { visitDao.addVisit(visitInput, mockk()) }
        val persisted = testEntityManager.find(Visit::class.java, result.id!!)

        assertNotNull(persisted.icdFoundationCode.find { it == "some url!" })
        assertNotNull(persisted.icdFoundationCode.find { it == "some other url!" })
        assertEquals(persisted.event, event)
        assertEquals("Some visit", result.title)
        assertEquals("Some visit", persisted.title)
    }
}