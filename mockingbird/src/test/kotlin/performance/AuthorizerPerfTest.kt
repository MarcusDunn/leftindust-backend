package performance

import com.leftindust.mockingbird.MockingbirdApplication
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(classes = [MockingbirdApplication::class])
@Tag("Performance")
class AuthorizerPerfTest {
    @Test
    internal fun `test getAuthorization`() {
        TODO("Not yet implemented")
    }
}