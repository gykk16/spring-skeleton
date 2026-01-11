package io.glory.common.utils

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.longs.shouldBeGreaterThanOrEqual
import io.kotest.matchers.longs.shouldBeLessThan
import io.kotest.matchers.shouldBe
import java.lang.Thread.sleep

class StopWatchTest : DescribeSpec({

    describe("stopWatch") {

        context("with title parameter") {
            it("should return Pair of elapsed time and function result") {
                // given
                val expectedResult = "test result"
                val testFunction = { expectedResult }

                // when
                val (elapsedMs, result) = stopWatch("Test Function", testFunction)

                // then
                result shouldBe expectedResult
                elapsedMs shouldBeGreaterThanOrEqual 0
            }

            it("should measure execution time accurately") {
                // given
                val delayMillis = 100L
                val testFunction = {
                    sleep(delayMillis)
                    "completed"
                }

                // when
                val (elapsedMs, result) = stopWatch("Long Running Test", testFunction)

                // then
                result shouldBe "completed"
                elapsedMs shouldBeGreaterThanOrEqual delayMillis
                elapsedMs shouldBeLessThan delayMillis + 100 // Allow 100ms tolerance
            }

            it("should propagate exception while still measuring time") {
                // given
                val expectedException = RuntimeException("Test exception")
                val testFunction = {
                    sleep(50)
                    throw expectedException
                }

                // when & then
                val exception = shouldThrow<RuntimeException> {
                    stopWatch("Exception Test", testFunction)
                }
                exception shouldBe expectedException
            }

            it("should handle null return values") {
                // given
                val testFunction: () -> String? = { null }

                // when
                val (elapsedMs, result) = stopWatch("Null Return Test", testFunction)

                // then
                result shouldBe null
                elapsedMs shouldBeGreaterThanOrEqual 0
            }

            it("should work with different return types") {
                // given
                val intFunction = { 42 }
                val listFunction = { listOf(1, 2, 3) }
                val mapFunction = { mapOf("key" to "value") }

                // when
                val (_, intResult) = stopWatch("Int Test", intFunction)
                val (_, listResult) = stopWatch("List Test", listFunction)
                val (_, mapResult) = stopWatch("Map Test", mapFunction)

                // then
                intResult shouldBe 42
                listResult shouldBe listOf(1, 2, 3)
                mapResult shouldBe mapOf("key" to "value")
            }
        }

        context("without title parameter") {
            it("should return Pair using class name as title") {
                // given
                val expectedResult = "result without title"
                val testFunction = { expectedResult }

                // when
                val (elapsedMs, result) = stopWatch(testFunction)

                // then
                result shouldBe expectedResult
                elapsedMs shouldBeGreaterThanOrEqual 0
            }

            it("should propagate exceptions without title") {
                // given
                val expectedException = IllegalArgumentException("Invalid argument")
                val testFunction = { throw expectedException }

                // when & then
                val exception = shouldThrow<IllegalArgumentException> {
                    stopWatch(testFunction)
                }
                exception shouldBe expectedException
            }
        }

        context("elapsed time accuracy") {
            it("should return elapsed time close to actual execution time") {
                // given
                val delays = listOf(10L, 50L, 100L)

                // when & then
                delays.forEach { delay ->
                    val (elapsedMs, _) = stopWatch("Delay $delay") {
                        sleep(delay)
                        "done"
                    }
                    elapsedMs shouldBeGreaterThanOrEqual delay
                    elapsedMs shouldBeLessThan delay + 50 // 50ms tolerance
                }
            }

            it("should return near-zero elapsed time for fast functions") {
                // given
                val fastFunction = { 1 + 1 }

                // when
                val (elapsedMs, result) = stopWatch("Fast Function", fastFunction)

                // then
                result shouldBe 2
                elapsedMs shouldBeLessThan 10 // Should be nearly instant
            }
        }

        context("concurrent execution") {
            it("should handle multiple sequential executions with independent timing") {
                // given
                val functions = (1..5).map { index ->
                    {
                        sleep(10)
                        "Result $index"
                    }
                }

                // when
                val results = functions.mapIndexed { index, function ->
                    stopWatch("Sequential Test $index", function)
                }

                // then
                results.forEachIndexed { index, (elapsedMs, result) ->
                    result shouldBe "Result ${index + 1}"
                    elapsedMs shouldBeGreaterThanOrEqual 10
                }
            }
        }
    }
})
