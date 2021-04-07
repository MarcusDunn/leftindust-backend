package performance

import com.leftindust.mockingbird.MockingbirdApplication
import com.leftindust.mockingbird.auth.Authorizer
import com.leftindust.mockingbird.auth.Crud
import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.dao.Tables
import com.leftindust.mockingbird.dao.entity.Action
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(classes = [MockingbirdApplication::class])
@Tag("Performance")
class AuthorizerPerfTest {
    @Test
    internal fun `test getAuthorization`(@Autowired authorizer: Authorizer) {
        val action = Action(Crud.READ to Tables.Patient)
        val authToken = object : MediqToken {
            override val uid = "admin"
            override fun isVerified() = true
        }

        assertPerf(
            name = "getAuthorization",
            runs = 1000,
            maxNanos = 600_000
        ) {
            runBlocking {
                authorizer.getAuthorization(action, authToken)
            }
        }
    }
}