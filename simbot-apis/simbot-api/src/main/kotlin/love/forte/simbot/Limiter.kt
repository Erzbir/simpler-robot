/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
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

package love.forte.simbot


import kotlinx.coroutines.flow.*
import love.forte.simbot.Limiter.ZERO
import java.util.stream.Stream
import kotlin.experimental.ExperimentalTypeInference

/**
 *
 * 一个**限流器**。
 *
 * 通俗一点的理解，限流器可以理解为一个用于需要进行 **数量限制** 或者说需要 **分页** 的地方，常见于一些返回值为 [Flow], [Sequence] 或者 [Stream] 之类的地方。
 *
 * 对于限流的具体实现细节由对应功能的实现者自行决定（包括是否真的进行分页等）。
 *
 * [Flow] 有提供 [Flow.take] 、[Stream] 有提供 [Stream.limit] 之类的函数，但是 [Limiter] 更多的是为功能提供一个初始量。
 * 有些情况下提供额外的Limiter参数可以对结果的获取有所优化，而有些情况下，使用 Limiter 的效果与直接使用流的API (例如[Flow.take]) 并无区别。
 *
 * ## 有效数字
 * [Limiter] 中，约定其所有有效数字均应大于 `0`。
 *
 * ## 默认实现
 * [Limiter] 提供两个默认实现：
 * - [ZERO] 所有数值恒为 `0` 的伴生实现，可直接作为默认值使用。
 * - 通过 [limiter.of(...)][Limiter.of] (for java) 或者 [limiter(...)][limiter] (for kotlin) 得到一个默认的数据类实现。
 *
 * ## 其他实现
 * 对于默认实现无法满足需求的情况，需要进行独立的实现，这一般由核心或者其他组件提供，并会整合在其他参数类型中，
 * 例如存在一个 `Query` 类型，它也许就会为了支持可限流的情况而对 Limiter 进行实现。
 *
 * ## 扩展
 * Limiter提供了一些扩展函数来快速对 [flow][Flow]、[sequence][Sequence]、[java stream][Stream] 进行操作, 以flow为例：
 *
 * ```kotlin
 * val limiter: Limiter = ...
 * val flow1 = Limiter.toFlow { batchSize ->
 *      println("batchSize: $batchSize")
 *      emit(1)
 *      emit(2)
 *      emit(3)
 *  }
 *
 * val flow2 = flow { ... }.withLimiter(limiter)
 * ```
 *
 * ## 解构
 * [Limiter] 支持对 [offset]、[limit]、[batchSize] 进行解构：
 *
 * ```kotlin
 * val (offset, limit, batchSize) = limiter
 * ```
 *
 * @see ZERO
 * @see limiter
 *
 * @see withLimiter
 * @see toFlow
 * @see toSequence
 *
 * @author ForteScarlet
 */
public interface Limiter {
    /**
     * 偏移量，即从第 [offset] 条数据开始返回。
     * 偏移量预期中的基数为 `0`, 即如果为 `0` 或者小于 `0`, 则代表从第一条数据开始获取。
     */
    public val offset: Int
    
    /**
     * 限流数量，即本次所得数据量最大不应超过此限制。
     * 例如 `limit = 10`, 那么返回值结果中的最终元素数量应当 `<= 10`.
     *
     * 当 `limit <= 0` 的时候，可认为返回值不受限制。
     *
     * 对于极少的情况，limit = 0 是存在特殊含义的时候，实现方应当有所说明。
     *
     */
    public val limit: Int
    
    /**
     * 对于部分平台的实现中，很有可能是分批次查询来获取结果的。
     * 当平台支持的时候，可以通过 [batchSize] 来指定一个批次大小来限制其内部每次对于API的请求数量。
     *
     * 正常情况下，此值只有在 >0 的时候生效。
     */
    public val batchSize: Int
    
    /**
     * [Limiter] 的默认值实现，[offset][ZERO.offset] 与 [limit][ZERO.limit] 均恒等于 `0`。
     *
     */
    public companion object ZERO : Limiter {
        override val offset: Int get() = 0
        override val limit: Int get() = 0
        override val batchSize: Int get() = 0
        
        /**
         * 根据参数得到 [Limiter] 实例。
         *
         * @param offset 偏移量。默认为 [ZERO.offset].
         * @param limit 元素数，默认为 [ZERO.limit].
         * @param batchSize 批次大小，默认为 [ZERO.batchSize].
         */
        @Api4J
        @JvmStatic
        @JvmOverloads
        public fun of(offset: Int = ZERO.offset, limit: Int = ZERO.limit, batchSize: Int = ZERO.batchSize): Limiter =
            if (offset <= 0 && limit <= 0 && batchSize <= 0) ZERO else LimiterImpl(offset, limit, batchSize)
        
        /**
         * 根据分页信息计算得到limiter。
         *
         * 分页以 `0` 作为起始页码
         *
         * 如果参数为无效参数（`pageSize <= 0 && pageNum < 0`）则返回 [ZERO].
         */
        public fun ofPage(pageSize: Int, pageNum: Int): Limiter {
            if (pageSize <= 0 && pageNum < 0) return ZERO
            return LimiterImpl(pageSize * pageNum, pageSize, 0)
        }
    }
}

//region 解构扩展
/**
 * [Limiter] 解构扩展，第1个属性，相当于 [Limiter.offset]。
 * ```kotlin
 * val (offset, limit, batchSize) = limiter
 * ```
 */
@Suppress("NOTHING_TO_INLINE")
public inline operator fun Limiter.component1(): Int = offset

/**
 * [Limiter] 解构扩展，第2个属性，相当于 [Limiter.limit]。
 * ```kotlin
 * val (offset, limit, batchSize) = limiter
 * ```
 */
@Suppress("NOTHING_TO_INLINE")
public inline operator fun Limiter.component2(): Int = limit

/**
 * [Limiter] 解构扩展，第3个属性，相当于 [Limiter.batchSize]。
 * ```kotlin
 * val (offset, limit, batchSize) = limiter
 * ```
 */
@Suppress("NOTHING_TO_INLINE")
public inline operator fun Limiter.component3(): Int = batchSize
//endregion


/**
 * 得到一个 [Limiter] 的默认实现。
 */
public fun limiter(offset: Int = ZERO.offset, limit: Int = ZERO.limit, batchSize: Int = ZERO.batchSize): Limiter =
    if (offset <= 0 && limit <= 0 && batchSize <= 0) Limiter else LimiterImpl(offset, limit, batchSize)

/**
 * 得到当前 [Limiter] 中等价的分页属性：页码大小。相当于 [Limiter.limit].
 */
public inline val Limiter.pageSize: Int get() = limit

/**
 * 得到当前 [Limiter] 中等价的分页属性：每页数量。计算方式大概类似于 `pageNum = offset / pageSize`.
 */
public inline val Limiter.pageNum: Int get() = if (offset <= 0 || pageSize <= 0) 0 else offset / pageSize  // offset = ps * pn, pn = offset / ps


private data class LimiterImpl(override val offset: Int, override val limit: Int, override val batchSize: Int) :
    Limiter {
    override fun toString(): String = "Limiter(offset=$offset, limit=$limit, batchSize=$batchSize)"
}

/**
 * 使用 [limiter] 对目标 [Stream] 进行限流。
 */
public fun <T> Stream<T>.withLimiter(limiter: Limiter): Stream<T> =
    let {
        with(limiter.offset) { if (this > 0) skip(toLong()) else it }
    }.let {
        with(limiter.limit) { if (this > 0) limit(toLong()) else it }
    }

/**
 * 使用 [limiter] 对目标 [Flow] 进行限流。
 */
public fun <T> Flow<T>.withLimiter(limiter: Limiter): Flow<T> =
    let {
        with(limiter.offset) { if (this > 0) drop(this) else it }
    }.let {
        with(limiter.limit) { if (this > 0) take(this) else it }
    }

/**
 * 使用 [limiter] 对目标 [Sequence] 进行限流。
 */
public fun <T> Sequence<T>.withLimiter(limiter: Limiter): Sequence<T> =
    let {
        with(limiter.offset) { if (this > 0) drop(this) else it }
    }.let {
        with(limiter.limit) { if (this > 0) take(this) else it }
    }

/**
 * 参数提供 [Limiter.batchSize], receiver为 [FlowCollector], flow的结果通过 [withLimiter] 限流
 */
@OptIn(ExperimentalTypeInference::class)
public inline fun <T> Limiter.toFlow(@BuilderInference crossinline collector: suspend FlowCollector<T>.(batchSize: Int) -> Unit): Flow<T> =
    flow { collector(batchSize) }.withLimiter(this)

/**
 * 参数提供 [Limiter.batchSize], receiver为 [SequenceScope], sequence的结果通过 [withLimiter] 限流
 */
@OptIn(ExperimentalTypeInference::class)
public inline fun <T> Limiter.toSequence(@BuilderInference crossinline block: suspend SequenceScope<T>.(batchSize: Int) -> Unit): Sequence<T> =
    sequence { block(batchSize) }.withLimiter(this)

