package performance

fun assertPerf(name: String, runs: Int, maxNanos: Int, test: (Int) -> Unit) {
    val startTime = System.nanoTime()
    for (i in 0 until runs) {
        test(i)
    }
    val totalTime = System.nanoTime() - startTime
    val averageTime = totalTime / runs
    assert(maxNanos > averageTime) { "$name was expected to average faster than ${maxNanos / 1000000F} ms but took ${averageTime / 1000000F} ms" }
    println("$name on average took ${maxNanos / 1000000F} ms over ${averageTime / 1000000F} ms runs")
}