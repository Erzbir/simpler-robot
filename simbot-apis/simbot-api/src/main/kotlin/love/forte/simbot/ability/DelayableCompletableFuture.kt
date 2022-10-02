/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x ) 的一部分。
 *
 *  simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 */

package love.forte.simbot.ability

import kotlinx.coroutines.CoroutineScope
import love.forte.simbot.JavaDuration
import java.util.concurrent.*
import java.util.function.BiConsumer
import java.util.function.BiFunction
import java.util.function.Consumer
import java.util.function.Function

/**
 * 可以链式调用延迟函数的 [CompletableFuture] 函数实现。
 *
 * [DelayableCompletableFuture] 由 [DelayableCoroutineScope]
 * 的相关api得到，实现 [Future] 和 [CompletionStage]，提供与 [CompletableFuture]
 * 基本一致的使用方式（但不直接实现 [CompletableFuture]），且允许通过 [DelayableCompletableFuture.asCompletableFuture]
 * 得到一个行为一致的 [CompletableFuture] 对象。
 *
 *
 */
public interface DelayableCompletableFuture<T> : Future<T>, CompletionStage<T> {
    
    /**
     * 得到用于描述当前 [DelayableCompletableFuture] 的 [CompletableFuture] 对象。
     */
    @Deprecated("Just use toCompletableFuture()", ReplaceWith("toCompletableFuture()"))
    public fun asCompletableFuture(): CompletableFuture<T> = toCompletableFuture()
    
    /**
     * 得到 [CompletableFuture] 对象。
     */
    override fun toCompletableFuture(): CompletableFuture<T>
    
    // region delay stage
    /**
     * 当等待的计算结果（通过 [get] 可以得到的结果 ）计算完成后，
     * 延迟指定时间周期 [duration]，并执行 [runnable] 函数。
     *
     * [delay] 与 [CompletionStage] 中所提供的其他函数不同的是，
     * [delay] 使用的是某个构建者提供的 [协程作用域][CoroutineScope]
     * 来进行延迟，其生命周期与此作用域一致，而 [CompletionStage]
     * 中其他异步函数则由 [CompletableFuture] 中实现的情况为准，
     * 与 [delay] 所使用的作用域无关。
     */
    public fun delay(duration: JavaDuration, runnable: Runnable): DelayableCompletableFuture<T>
    
    
    /**
     * 当等待的计算结果（通过 [get] 可以得到的结果 ）计算完成后，
     * 延迟指定时间单位为 [timeUnit] 的时间周期 [time]，并执行 [runnable] 函数。
     *
     * [delay] 与 [CompletionStage] 中所提供的其他函数不同的是，
     * [delay] 使用的是某个构建者提供的 [协程作用域][CoroutineScope]
     * 来进行延迟，其生命周期与此作用域一致，而 [CompletionStage]
     * 中其他异步函数则由 [CompletableFuture] 中实现的情况为准，
     * 与 [delay] 所使用的作用域无关。
     */
    public fun delay(time: Long, timeUnit: TimeUnit, runnable: Runnable): DelayableCompletableFuture<T>
    
    /**
     * 当等待的计算结果（通过 [get] 可以得到的结果 ）计算完成后，
     * 延迟指定时间单位为 [TimeUnit.MILLISECONDS] 的时间周期 [time]，并执行 [runnable] 函数。
     *
     * [delay] 与 [CompletionStage] 中所提供的其他函数不同的是，
     * [delay] 使用的是某个构建者提供的 [协程作用域][CoroutineScope]
     * 来进行延迟，其生命周期与此作用域一致，而 [CompletionStage]
     * 中其他异步函数则由 [CompletableFuture] 中实现的情况为准，
     * 与 [delay] 所使用的作用域无关。
     */
    public fun delay(time: Long, runnable: Runnable): DelayableCompletableFuture<T>
    
    /**
     * 当等待的计算结果（通过 [get] 可以得到的结果 ）计算完成后，
     * 延迟指定时间周期 [duration]，并执行 [function] 函数。
     *
     * [function] 函数的参数即为当前 [DelayableCompletableFuture] 的计算结果。
     *
     * [delayAndCompute] 与 [CompletionStage] 中所提供的其他函数不同的是，
     * [delayAndCompute] 使用的是某个构建者提供的 [协程作用域][CoroutineScope]
     * 来进行延迟，其生命周期与此作用域一致，而 [CompletionStage]
     * 中其他异步函数则由 [CompletableFuture] 中实现的情况为准，
     * 与 [delayAndCompute] 所使用的作用域无关。
     */
    public fun <V> delayAndCompute(
        duration: JavaDuration,
        function: Function<T, V>,
    ): DelayableCompletableFuture<V>
    
    /**
     * 当等待的计算结果（通过 [get] 可以得到的结果 ）计算完成后，
     * 延迟指定时间单位为 [TimeUnit.MILLISECONDS] 的时间周期 [time]，并执行 [function] 函数。
     *
     * [function] 函数的参数即为当前 [DelayableCompletableFuture] 的计算结果。
     *
     * [delayAndCompute] 与 [CompletionStage] 中所提供的其他函数不同的是，
     * [delayAndCompute] 使用的是某个构建者提供的 [协程作用域][CoroutineScope]
     * 来进行延迟，其生命周期与此作用域一致，而 [CompletionStage]
     * 中其他异步函数则由 [CompletableFuture] 中实现的情况为准，
     * 与 [delayAndCompute] 所使用的作用域无关。
     */
    public fun <V> delayAndCompute(
        time: Long,
        timeUnit: TimeUnit,
        function: Function<T, V>,
    ): DelayableCompletableFuture<V>
    
    /**
     * 当等待的计算结果（通过 [get] 可以得到的结果 ）计算完成后，
     * 延迟指定时间单位为 [TimeUnit.MILLISECONDS] 的时间周期 [time]，并执行 [function] 函数。
     *
     * [function] 函数的参数即为当前 [DelayableCompletableFuture] 的计算结果。
     *
     * [delayAndCompute] 与 [CompletionStage] 中所提供的其他函数不同的是，
     * [delayAndCompute] 使用的是某个构建者提供的 [协程作用域][CoroutineScope]
     * 来进行延迟，其生命周期与此作用域一致，而 [CompletionStage]
     * 中其他异步函数则由 [CompletableFuture] 中实现的情况为准，
     * 与 [delayAndCompute] 所使用的作用域无关。
     */
    public fun <V> delayAndCompute(
        time: Long,
        function: Function<T, V>,
    ): DelayableCompletableFuture<V>
    // endregion
    
    // region CompletionStage
    override fun <U : Any?> thenApply(fn: Function<in T, out U>): DelayableCompletableFuture<U>
    
    override fun <U : Any?> thenApplyAsync(fn: Function<in T, out U>): DelayableCompletableFuture<U>
    
    override fun <U : Any?> thenApplyAsync(
        fn: Function<in T, out U>,
        executor: Executor,
    ): DelayableCompletableFuture<U>
    
    override fun thenAccept(action: Consumer<in T>): DelayableCompletableFuture<Void?>
    
    override fun thenAcceptAsync(action: Consumer<in T>): DelayableCompletableFuture<Void?>
    
    override fun thenAcceptAsync(action: Consumer<in T>, executor: Executor): DelayableCompletableFuture<Void?>
    
    override fun thenRun(action: Runnable): DelayableCompletableFuture<Void?>
    
    override fun thenRunAsync(action: Runnable): DelayableCompletableFuture<Void?>
    
    override fun thenRunAsync(action: Runnable, executor: Executor): DelayableCompletableFuture<Void?>
    
    
    override fun <U : Any?> thenAcceptBoth(
        other: CompletionStage<out U>,
        action: BiConsumer<in T, in U>,
    ): DelayableCompletableFuture<Void?>
    
    override fun <U : Any?> thenAcceptBothAsync(
        other: CompletionStage<out U>,
        action: BiConsumer<in T, in U>,
    ): DelayableCompletableFuture<Void?>
    
    override fun <U : Any?> thenAcceptBothAsync(
        other: CompletionStage<out U>,
        action: BiConsumer<in T, in U>,
        executor: Executor,
    ): DelayableCompletableFuture<Void?>
    
    override fun <U : Any?, V : Any?> thenCombine(
        other: CompletionStage<out U>,
        fn: BiFunction<in T, in U, out V>,
    ): DelayableCompletableFuture<V>
    
    override fun <U : Any?, V : Any?> thenCombineAsync(
        other: CompletionStage<out U>,
        fn: BiFunction<in T, in U, out V>,
    ): DelayableCompletableFuture<V>
    
    override fun <U : Any?, V : Any?> thenCombineAsync(
        other: CompletionStage<out U>,
        fn: BiFunction<in T, in U, out V>,
        executor: Executor,
    ): DelayableCompletableFuture<V>
    
    override fun runAfterBoth(other: CompletionStage<*>, action: Runnable): DelayableCompletableFuture<Void?>
    
    override fun runAfterBothAsync(other: CompletionStage<*>, action: Runnable): DelayableCompletableFuture<Void?>
    
    override fun runAfterBothAsync(
        other: CompletionStage<*>,
        action: Runnable,
        executor: Executor,
    ): DelayableCompletableFuture<Void?>
    
    override fun <U : Any?> applyToEither(
        other: CompletionStage<out T>,
        fn: Function<in T, U>,
    ): DelayableCompletableFuture<U>
    
    override fun <U : Any?> applyToEitherAsync(
        other: CompletionStage<out T>,
        fn: Function<in T, U>,
    ): DelayableCompletableFuture<U>
    
    override fun <U : Any?> applyToEitherAsync(
        other: CompletionStage<out T>,
        fn: Function<in T, U>,
        executor: Executor,
    ): DelayableCompletableFuture<U>
    
    override fun acceptEither(
        other: CompletionStage<out T>,
        action: Consumer<in T>,
    ): DelayableCompletableFuture<Void?>
    
    override fun acceptEitherAsync(
        other: CompletionStage<out T>,
        action: Consumer<in T>,
    ): DelayableCompletableFuture<Void?>
    
    override fun acceptEitherAsync(
        other: CompletionStage<out T>,
        action: Consumer<in T>,
        executor: Executor,
    ): DelayableCompletableFuture<Void?>
    
    override fun runAfterEither(other: CompletionStage<*>, action: Runnable): DelayableCompletableFuture<Void?>
    
    override fun runAfterEitherAsync(other: CompletionStage<*>, action: Runnable): DelayableCompletableFuture<Void?>
    
    override fun runAfterEitherAsync(
        other: CompletionStage<*>,
        action: Runnable,
        executor: Executor,
    ): DelayableCompletableFuture<Void?>
    
    override fun <U : Any?> thenCompose(fn: Function<in T, out CompletionStage<U>>): DelayableCompletableFuture<U>
    
    override fun <U : Any?> thenComposeAsync(fn: Function<in T, out CompletionStage<U>>): DelayableCompletableFuture<U>
    
    override fun <U : Any?> thenComposeAsync(
        fn: Function<in T, out CompletionStage<U>>,
        executor: Executor,
    ): DelayableCompletableFuture<U>
    
    override fun exceptionally(fn: Function<Throwable, out T>): DelayableCompletableFuture<T>
    
    override fun whenComplete(action: BiConsumer<in T?, in Throwable?>): DelayableCompletableFuture<T>
    
    override fun whenCompleteAsync(action: BiConsumer<in T?, in Throwable?>): DelayableCompletableFuture<T>
    
    override fun whenCompleteAsync(
        action: BiConsumer<in T?, in Throwable?>,
        executor: Executor,
    ): DelayableCompletableFuture<T>
    
    override fun <U : Any?> handle(fn: BiFunction<in T?, Throwable?, out U>): DelayableCompletableFuture<U>
    
    override fun <U : Any?> handleAsync(fn: BiFunction<in T?, Throwable?, out U>): DelayableCompletableFuture<U>
    
    override fun <U : Any?> handleAsync(
        fn: BiFunction<in T?, Throwable?, out U>,
        executor: Executor,
    ): DelayableCompletableFuture<U>
    // endregion
    
    
}



