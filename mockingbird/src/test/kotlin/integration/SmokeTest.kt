package integration

import com.leftindust.mockingbird.MockingbirdApplication
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import javax.sql.DataSource

@Tag("Integration")
@SpringBootTest(classes = [MockingbirdApplication::class])
class SmokeTest {
    @Test
    internal fun contextLoads() {
        // checks that spring boot context loads
    }

    @Test
    internal fun `database connected`(@Autowired dataSource: DataSource) {
        assertEquals(true, dataSource.connection.isValid(5))
    }
}