package integration

import com.leftindust.condor.CondorApplication
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(classes = [CondorApplication::class])
@Tag("Integration")
internal class CondorApplicationKtTest {
    @Test
    internal fun `context loads`() {
    }
}