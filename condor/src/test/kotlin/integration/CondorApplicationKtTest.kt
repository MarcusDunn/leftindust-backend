package integration

import com.leftindust.condor.CondorApplication
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import javax.sql.DataSource

@SpringBootTest(classes = [CondorApplication::class])
@Tag("Integration")
internal class CondorApplicationKtTest {
    @Test
    internal fun `context loads`() {
    }

    @Test
    internal fun `connected to database`(@Autowired dataSource: DataSource) {
        assertEquals(true, dataSource.connection.isValid(5))
    }
}