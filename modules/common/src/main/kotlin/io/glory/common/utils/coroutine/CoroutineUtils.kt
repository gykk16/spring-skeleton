package io.glory.common.utils.coroutine

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlinx.coroutines.*
import kotlinx.coroutines.slf4j.MDCContext

private val logger = KotlinLogging.logger {}

/**
 * MDC 컨텍스트를 전파하는 runBlocking
 *
 * MDC가 코루틴 내부로 전파됩니다.
 *
 * ## 사용 예시
 * ```kotlin
 * // 기본 사용
 * runBlockingWithMDC {
 *     val result1 = asyncWithMDC { fetchData1() }
 *     val result2 = asyncWithMDC { fetchData2() }
 *     result1.await() + result2.await()
 * }
 *
 * // 커스텀 dispatcher 사용
 * runBlockingWithMDC(Dispatchers.IO) {
 *     // IO dispatcher에서 실행
 * }
 * ```
 *
 * @param context 코루틴 컨텍스트 (기본값: EmptyCoroutineContext)
 * @param block 실행할 suspend 블록
 * @return 블록의 실행 결과
 */
fun <T> runBlockingWithMDC(
    context: CoroutineContext = EmptyCoroutineContext,
    block: suspend CoroutineScope.() -> T,
): T = runBlocking(context + MDCContext(), block)

/**
 * 현재 scope 내에서 MDC를 전파하는 async 실행 (Structured Concurrency 유지)
 *
 * 부모 코루틴이 취소되면 이 코루틴도 취소됩니다.
 *
 * @param context 추가 코루틴 컨텍스트 (MDCContext와 결합됨)
 * @param start 코루틴 시작 옵션
 * @param block 실행할 suspend 블록
 * @return Deferred 결과
 */
fun <T> CoroutineScope.asyncWithMDC(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> T,
): Deferred<T> = async(context + MDCContext(), start, block)

/**
 * 현재 scope 내에서 MDC를 전파하는 launch 실행 (Structured Concurrency 유지)
 *
 * 부모 코루틴이 취소되면 이 코루틴도 취소됩니다.
 *
 * @param context 추가 코루틴 컨텍스트 (MDCContext와 결합됨)
 * @param start 코루틴 시작 옵션
 * @param block 실행할 suspend 블록
 * @return Job
 */
fun CoroutineScope.launchWithMDC(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit,
): Job = launch(context + MDCContext(), start, block)

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
