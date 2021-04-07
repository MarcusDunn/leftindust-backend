package performance

import com.leftindust.mockingbird.MockingbirdApplication
import com.leftindust.mockingbird.external.icd.IcdFetcher
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(classes = [MockingbirdApplication::class])
@Tag("Performance")
class IcdPerfTest {
    @Test
    internal fun `test 1000 icd linearization searches`(@Autowired icdFetcher: IcdFetcher) {
        assertPerf(
            name = "icd linearization search",
            runs = 1000,
            maxNanos = 4_000_000
        ) {
            runBlocking {
                icdFetcher.linearizationSearch(
                    query = "covid",
                    flatResults = false,
                    linearizationName = "mms",
                    releaseId = "2020-09"
                ).getOrThrow()
            }
        }
    }
}
