package io.glory.common.utils

import io.github.oshai.kotlinlogging.KotlinLogging

@PublishedApi
internal val stopWatchLogger = KotlinLogging.logger {}

@PublishedApi
internal const val NANOS_PER_MILLI = 1_000_000L

/**
 * Measures and logs the process time of the function.
 *
 * @param function a function that takes no arguments and returns a value of type [T].
 * @return Pair of (elapsed time in milliseconds, function result)
 */
inline fun <T> stopWatch(function: () -> T): Pair<Long, T> {
    return stopWatch("stopWatch", function)
}

/**
 * Measures and logs the process time of the function.
 * Uses System.nanoTime() for high-precision, low-overhead timing.
 *
 * @param title the title of the process.
 * @param function a function that takes no arguments and returns a value of type [T].
 * @return Pair of (elapsed time in milliseconds, function result)
 */
inline fun <T> stopWatch(title: String, function: () -> T): Pair<Long, T> {
    stopWatchLogger.info { "# >>> $title" }
    val startNanos = System.nanoTime()

    return try {
        val result = function()
        val elapsedMs = (System.nanoTime() - startNanos) / NANOS_PER_MILLI
        stopWatchLogger.info { "# <<< $title , elapsed: ${elapsedMs}ms" }
        elapsedMs to result
    } catch (e: Exception) {
        val elapsedMs = (System.nanoTime() - startNanos) / NANOS_PER_MILLI
        stopWatchLogger.info { "# <<< $title , elapsed: ${elapsedMs}ms (exception)" }
        throw e
    }
}