package io.glory.testsupport

/**
 * Utility for measuring test execution time.
 *
 * Usage:
 * ```kotlin
 * val elapsed = TestTimeRunner.timeTest(1000, "my operation") {
 *     myService.process()
 *     "done"
 * }
 * ```
 */
object TestTimeRunner {

    fun timeTest(loopCount: Int, testName: String, block: () -> Any): Long {
        val start = System.currentTimeMillis()
        repeat(loopCount) { block() }
        val elapsed = System.currentTimeMillis() - start
        println("==> $testName , elapsed = $elapsed ms")
        return elapsed
    }
}
