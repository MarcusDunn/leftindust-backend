package integration.dao.entity

import com.leftindust.mockingbird.MockingbirdApplication
import com.leftindust.mockingbird.dao.entity.Phone
import com.leftindust.mockingbird.graphql.types.GraphQLPhoneType
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import javax.persistence.EntityManager
import javax.transaction.Transactional

@SpringBootTest(classes = [MockingbirdApplication::class])
@Tag("Integration")
@Transactional
internal class PhoneTest(
    @Autowired private val entityManager: EntityManager
) {

    @Test
    internal fun `insert phone`() {
        val phone = Phone(number = "(604) 823 8781", type = GraphQLPhoneType.Other)
        entityManager.persist(phone)
    }
}