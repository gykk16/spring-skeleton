package io.glory.common.utils.coroutine

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.*
import kotlinx.coroutines.slf4j.MDCContext
import java.io.Closeable
import java.util.concurrent.Executors

private val logger = KotlinLogging.logger {}

/**
 * 코루틴 유틸리티
 *
 * Virtual Thread 기반의 코루틴 실행과 MDC 전파를 지원합니다.
 *
 * ## 사용 예시
 * ```kotlin
 * // Structured concurrency 유지
 * CoroutineUtils.runBlockingWithMDC {
 *     val result1 = asyncWithMDC { fetchData1() }
 *     val result2 = asyncWithMDC { fetchData2() }
 *     result1.await() + result2.await()
 * }
 *
 * // Fire-and-forget (결과 필요 없음)
 * CoroutineUtils.launchDetachedWithMDC { sendNotification() }
 *
 * // 독립적 async (결과 필요함)
 * val deferred = CoroutineUtils.asyncDetachedWithMDC { fetchExternalData() }
 * val result = deferred.await()
 * ```
 */
object CoroutineUtils : Closeable {

    private val dispatcher = Executors.newVirtualThreadPerTaskExecutor().asCoroutineDispatcher()

    /**
     * MDC 컨텍스트를 전파하는 runBlocking
     *
     * Virtual Thread 기반 디스패처에서 실행되며, MDC가 코루틴 내부로 전파됩니다.
     *
     * @param block 실행할 suspend 블록
     * @return 블록의 실행 결과
     */
    fun <T> runBlockingWithMDC(block: suspend CoroutineScope.() -> T): T =
        runBlocking(dispatcher + MDCContext(), block)

    /**
     * 현재 scope 내에서 MDC를 전파하는 async 실행 (Structured Concurrency 유지)
     *
     * 부모 코루틴이 취소되면 이 코루틴도 취소됩니다.
     *
     * @param block 실행할 suspend 블록
     * @return Deferred 결과
     */
    fun <T> CoroutineScope.asyncWithMDC(
        block: suspend CoroutineScope.() -> T,
    ): Deferred<T> = async(MDCContext(), block = block)

    /**
     * 현재 scope 내에서 MDC를 전파하는 launch 실행 (Structured Concurrency 유지)
     *
     * 부모 코루틴이 취소되면 이 코루틴도 취소됩니다.
     *
     * @param block 실행할 suspend 블록
     * @return Job
     */
    fun CoroutineScope.launchWithMDC(
        block: suspend CoroutineScope.() -> Unit,
    ): Job = launch(MDCContext(), block = block)

    /**
     * 로깅이 포함된 coroutineScope
     *
     * 시작과 종료 시점에 스레드 ID와 함께 로그를 남깁니다.
     * 디버깅 목적으로 사용합니다.
     *
     * @param title 로그에 표시할 제목
     * @param block 실행할 suspend 블록
     * @return 블록의 실행 결과
     */
    suspend fun <R> withLogging(
        title: String,
        block: suspend CoroutineScope.() -> R,
    ): R = coroutineScope {
        val startThreadId = Thread.currentThread().threadId()
        logger.debug { "# >>> $title, start thread: $startThreadId" }
        try {
            block()
        } finally {
            val endThreadId = Thread.currentThread().threadId()
            logger.debug { "# <<< $title, end thread: $endThreadId" }
        }
    }

    /**
     * 디스패처 리소스 정리
     *
     * 애플리케이션 종료 시 호출해야 합니다.
     * Spring 환경에서는 @PreDestroy 또는 DisposableBean을 통해 호출하세요.
     */
    override fun close() {
        logger.info { "# ==> Closing CoroutineUtils dispatcher" }
        dispatcher.close()
    }
}
