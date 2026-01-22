package io.glory.common.utils.coroutine

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay
import org.slf4j.MDC
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class CoroutineUtilsTest : DescribeSpec({

    val traceIdKey = "traceId"

    beforeEach {
        MDC.clear()
    }

    afterEach {
        MDC.clear()
    }

    describe("runBlockingWithMDC") {

        context("MDC 전파") {
            it("should propagate MDC context into coroutine") {
                // given
                val expectedTraceId = "test-trace-id-12345"
                MDC.put(traceIdKey, expectedTraceId)

                // when
                val capturedTraceId = runBlockingWithMDC {
                    MDC.get(traceIdKey)
                }

                // then
                capturedTraceId shouldBe expectedTraceId
            }

            it("should preserve MDC after delay") {
                // given
                val expectedTraceId = "trace-with-delay"
                MDC.put(traceIdKey, expectedTraceId)

                // when
                val capturedTraceId = runBlockingWithMDC {
                    delay(50)
                    MDC.get(traceIdKey)
                }

                // then
                capturedTraceId shouldBe expectedTraceId
            }
        }

        context("결과 반환") {
            it("should return block result") {
                // given
                val expected = "test result"

                // when
                val result = runBlockingWithMDC { expected }

                // then
                result shouldBe expected
            }

            it("should work with different return types") {
                // when
                val intResult = runBlockingWithMDC { 42 }
                val listResult = runBlockingWithMDC { listOf(1, 2, 3) }
                val nullResult = runBlockingWithMDC<String?> { null }

                // then
                intResult shouldBe 42
                listResult shouldBe listOf(1, 2, 3)
                nullResult shouldBe null
            }
        }

    }

    describe("asyncWithMDC") {

        context("MDC 전파") {
            it("should propagate MDC context into async coroutine") {
                // given
                val expectedTraceId = "async-trace-id"
                MDC.put(traceIdKey, expectedTraceId)

                // when
                val capturedTraceId = runBlockingWithMDC {
                    val deferred = asyncWithMDC {
                        MDC.get(traceIdKey)
                    }
                    deferred.await()
                }

                // then
                capturedTraceId shouldBe expectedTraceId
            }

            it("should preserve MDC after delay in async") {
                // given
                val expectedTraceId = "async-delay-trace"
                MDC.put(traceIdKey, expectedTraceId)

                // when
                val capturedTraceId = runBlockingWithMDC {
                    val deferred = asyncWithMDC {
                        delay(50)
                        MDC.get(traceIdKey)
                    }
                    deferred.await()
                }

                // then
                capturedTraceId shouldBe expectedTraceId
            }
        }

        context("병렬 실행") {
            it("should execute multiple async coroutines in parallel") {
                // given
                val expectedTraceId = "parallel-trace"
                MDC.put(traceIdKey, expectedTraceId)

                // when
                val results = runBlockingWithMDC {
                    val deferred1 = asyncWithMDC {
                        delay(50)
                        "result1-${MDC.get(traceIdKey)}"
                    }
                    val deferred2 = asyncWithMDC {
                        delay(50)
                        "result2-${MDC.get(traceIdKey)}"
                    }
                    listOf(deferred1.await(), deferred2.await())
                }

                // then
                results shouldBe listOf("result1-$expectedTraceId", "result2-$expectedTraceId")
            }
        }
    }

    describe("launchWithMDC") {

        context("MDC 전파") {
            it("should propagate MDC context into launch coroutine") {
                // given
                val expectedTraceId = "launch-trace-id"
                MDC.put(traceIdKey, expectedTraceId)
                var capturedTraceId: String? = null
                val latch = CountDownLatch(1)

                // when
                runBlockingWithMDC {
                    launchWithMDC {
                        capturedTraceId = MDC.get(traceIdKey)
                        latch.countDown()
                    }
                }
                latch.await(1, TimeUnit.SECONDS)

                // then
                capturedTraceId shouldBe expectedTraceId
            }

            it("should preserve MDC after delay in launch") {
                // given
                val expectedTraceId = "launch-delay-trace"
                MDC.put(traceIdKey, expectedTraceId)
                var capturedTraceId: String? = null

                // when
                runBlockingWithMDC {
                    val job = launchWithMDC {
                        delay(50)
                        capturedTraceId = MDC.get(traceIdKey)
                    }
                    job.join()
                }

                // then
                capturedTraceId shouldBe expectedTraceId
            }
        }

        context("실행 완료") {
            it("should complete launch job") {
                // given
                var executed = false

                // when
                runBlockingWithMDC {
                    val job = launchWithMDC {
                        executed = true
                    }
                    job.join()
                }

                // then
                executed shouldBe true
            }
        }
    }

    describe("withLogging") {

        context("결과 반환") {
            it("should return block result") {
                // given
                val expected = "logged result"

                // when
                val result = runBlockingWithMDC {
                    withLogging("Test Block") { expected }
                }

                // then
                result shouldBe expected
            }

            it("should preserve MDC in withLogging block") {
                // given
                val expectedTraceId = "logging-trace"
                MDC.put(traceIdKey, expectedTraceId)

                // when
                val capturedTraceId = runBlockingWithMDC {
                    withLogging("MDC Test") {
                        MDC.get(traceIdKey)
                    }
                }

                // then
                capturedTraceId shouldBe expectedTraceId
            }
        }
    }

    describe("MDC 격리") {

        it("should not affect outer MDC after coroutine completes") {
            // given
            val outerTraceId = "outer-trace"
            MDC.put(traceIdKey, outerTraceId)

            // when
            runBlockingWithMDC {
                MDC.put(traceIdKey, "inner-trace")
            }

            // then
            MDC.get(traceIdKey) shouldBe outerTraceId
        }

        it("should handle empty MDC") {
            // given
            MDC.clear()

            // when
            val capturedTraceId = runBlockingWithMDC {
                MDC.get(traceIdKey)
            }

            // then
            capturedTraceId shouldBe null
        }
    }

    describe("복합 시나리오") {

        it("should propagate MDC through nested async and launch") {
            // given
            val expectedTraceId = "nested-trace"
            MDC.put(traceIdKey, expectedTraceId)
            val capturedIds = mutableListOf<String?>()

            // when
            runBlockingWithMDC {
                capturedIds.add(MDC.get(traceIdKey))

                val deferred = asyncWithMDC {
                    delay(10)
                    capturedIds.add(MDC.get(traceIdKey))

                    launchWithMDC {
                        delay(10)
                        capturedIds.add(MDC.get(traceIdKey))
                    }.join()

                    MDC.get(traceIdKey)
                }
                deferred.await()
            }

            // then
            capturedIds.forEach { it shouldBe expectedTraceId }
        }

        it("should preserve MDC in multiple parallel coroutines") {
            // given
            val expectedTraceId = "parallel-trace"
            MDC.put(traceIdKey, expectedTraceId)

            // when
            val traceIds = runBlockingWithMDC {
                val deferred1 = asyncWithMDC {
                    delay(10)
                    MDC.get(traceIdKey)
                }
                val deferred2 = asyncWithMDC {
                    delay(10)
                    MDC.get(traceIdKey)
                }
                listOf(deferred1.await(), deferred2.await())
            }

            // then
            traceIds.size shouldBe 2
            traceIds.forEach { it shouldBe expectedTraceId }
        }
    }
})
