package integration

import com.leftindust.mockingbird.MockingbirdApplication
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@Tag("Integration")
@SpringBootTest(classes = [MockingbirdApplication::class])
class SmokeTest {
    @Test
    internal fun contextLoads() {
        // checks that spring boot context loads
    }
}