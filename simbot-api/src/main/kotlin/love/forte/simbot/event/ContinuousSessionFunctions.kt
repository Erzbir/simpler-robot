/*
 * Copyright (c) 2022-2023 ForteScarlet <ForteScarlet@163.com>
 *
 * 本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x 、simbot3 等) 的一部分。
 * simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 * 发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 * 你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 * https://www.gnu.org/licenses
 * https://www.gnu.org/licenses/gpl-3.0-standalone.html
 * https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 */

package love.forte.simbot.event

import kotlinx.coroutines.CancellationException
import love.forte.simbot.Api4J
import love.forte.simbot.utils.runWithInterruptible


// region base listener
/**
 *
 * 相当于函数 `EventProcessingContext.(ContinuousSessionProvider<T>) -> Unit`。
 *
 * 对于Java使用者可以考虑使用 [BlockingContinuousSessionSelector]。
 *
 * @see BlockingContinuousSessionSelector
 */
public fun interface ContinuousSessionSelector<T> {
    @JvmSynthetic
    public suspend operator fun EventProcessingContext.invoke(provider: ContinuousSessionProvider<T>)
}

/**
 * 阻塞的 [ContinuousSessionSelector].
 */
@Api4J
public fun interface BlockingContinuousSessionSelector<T> {
    
    /**
     * 执行一个含义等同于 [ContinuousSessionSelector.invoke] 的阻塞函数。
     *
     * @throws CancellationException 执行被终止
     */
    public operator fun invoke(context: EventProcessingContext, provider: ContinuousSessionProvider<T>)
}


@OptIn(Api4J::class)
internal fun <T> BlockingContinuousSessionSelector<T>.parse(): ContinuousSessionSelector<T> =
    ContinuousSessionSelector { provider -> runWithInterruptible { this@parse(this, provider) } }
// endregion

// region event matcher
/**
 * 使用于 [ContinuousSessionContext.waitingForNext] 中的事件匹配器。
 *
 * 提供一个 [Event] 来判断其是否符合条件。
 *
 * 阻塞实现参考 [BlockingContinuousSessionEventMatcher].
 *
 */
public fun interface ContinuousSessionEventMatcher<in E : Event> {
    
    /**
     * 根据条件判断结果。
     */
    @JvmSynthetic
    public suspend operator fun EventProcessingContext.invoke(event: E): Boolean
    
    
    public companion object AlwaysTrue : ContinuousSessionEventMatcher<Event> {
        override suspend fun EventProcessingContext.invoke(event: Event): Boolean {
            return true
        }
    }
    
}

/**
 * 使用于 [ContinuousSessionContext.waitingForNext] 中的事件匹配器。
 *
 * 提供一个 [Event] 来判断其是否符合条件。
 *
 */
@Api4J
public fun interface BlockingContinuousSessionEventMatcher<in E : Event> {
    
    /**
     * 根据条件判断结果。
     */
    public operator fun EventProcessingContext.invoke(event: E): Boolean
    
    
    public companion object AlwaysTrue : BlockingContinuousSessionEventMatcher<Event> {
        override fun EventProcessingContext.invoke(event: Event): Boolean {
            return true
        }
    }
    
}

@OptIn(Api4J::class)
internal fun <E : Event> BlockingContinuousSessionEventMatcher<E>.parse(): ContinuousSessionEventMatcher<E> {
    return if (this === BlockingContinuousSessionEventMatcher.AlwaysTrue) {
        ContinuousSessionEventMatcher.AlwaysTrue
    } else {
        ContinuousSessionEventMatcher { event -> runWithInterruptible { invoke(event) } }
    }
}


// endregion
