/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  根据 GNU LESSER GENERAL PUBLIC LICENSE 3 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

package love.forte.simbot.event

import love.forte.simbot.message.doSafeCast
import love.forte.simbot.utils.runInBlocking


/**
 * 一个 **起点** 事件。
 *
 * [StartPointEvent] 是一个变化的起始变化，通常情况下其代表在变化后变化体开始存在，
 * 因此在 [StartPointEvent] 中 [before] 通常为 null。
 */
public interface StartPointEvent<S, V> : ChangedEvent<S, V?, V> {
    /**
     * 此次变化的目标。
     */
    public suspend fun target(): V

    override suspend fun source(): S
    override suspend fun before(): V? = null
    override suspend fun after(): V = target()

    override val before: V? get() = null
    override val after: V get() = target
    public val target: V get() = runInBlocking { target() }

    public companion object Key : BaseEventKey<StartPointEvent<*, *>>("api.start_point", ChangedEvent) {
        override fun safeCast(value: Any): StartPointEvent<*, *>? = doSafeCast(value)
    }
}


/**
 * 一个 **终点** 事件。
 *
 *  [EndPointEvent] 是一个变化的最终变化，通常情况下其代表在变化后变化体则不再存在，
 * 因此在 [EndPointEvent] 中，[after] 应为null。
 *
 */
public interface EndPointEvent<S, V> : ChangedEvent<S, V, V?> {
    /**
     * 此次变化的目标。
     */
    public suspend fun target(): V

    override suspend fun before(): V = target()
    override suspend fun after(): V? = null

    override val before: V get() = target
    override val after: V? get() = null
    public val target: V get() = runInBlocking { target() }

    public companion object Key : BaseEventKey<EndPointEvent<*, *>>("api.end_point", ChangedEvent) {
        override fun safeCast(value: Any): EndPointEvent<*, *>? = doSafeCast(value)
    }

}


/**
 * 一个 **增加** 事件，代表某种 [事物][target] 被增加到了一个 [源][source] 中。
 *
 *
 */
public interface IncreaseEvent<S, V> : StartPointEvent<S, V> {
    override suspend fun source(): S
    override suspend fun target(): V

    public companion object Key : BaseEventKey<IncreaseEvent<*, *>>("api.increase", StartPointEvent) {
        override fun safeCast(value: Any): IncreaseEvent<*, *>? = doSafeCast(value)
    }
}


/**
 * 一个 **减少** 事件，代表某种 [事物][target] 被从一个 [源][source] 中移除。
 *
 */
public interface DecreaseEvent<S, V> : EndPointEvent<S, V> {
    override suspend fun source(): S
    override suspend fun target(): V

    public companion object Key : BaseEventKey<DecreaseEvent<*, *>>("api.decrease", EndPointEvent) {
        override fun safeCast(value: Any): DecreaseEvent<*, *>? = doSafeCast(value)
    }
}