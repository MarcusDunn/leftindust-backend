package integration

import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@Tag("Integration")
internal class CondorApplicationKtTest {
    @Test
    internal fun `context loads`() {
    }
}