/*
 * Copyright (c) 2021-2023 ForteScarlet <ForteScarlet@163.com>
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

@file:JvmName("SimpleInterceptUtil")
@file:JvmMultifileClass

package love.forte.simbot.core.event

import love.forte.simbot.Api4J
import love.forte.simbot.Interceptor
import love.forte.simbot.PriorityConstant
import love.forte.simbot.event.*
import java.util.function.Function

@DslMarker
@Retention(AnnotationRetention.BINARY)
internal annotation class SimpleFunctionalProcessingInterceptorDSL

@DslMarker
@Retention(AnnotationRetention.BINARY)
internal annotation class SimpleFunctionalListenerInterceptorDSL

/**
 * 核心提供的事件拦截器实现, 基于函数提供外部事件逻辑。
 *
 * @see SimpleFunctionalEventProcessingInterceptor
 *
 */
public sealed class SimpleFunctionalEventInterceptor<C : EventInterceptor.Context<R>, R> : Interceptor<C, R> {
    public abstract val interceptFunction: suspend (C) -> R
    override suspend fun intercept(context: C): R = interceptFunction(context)
}


public class SimpleFunctionalEventProcessingInterceptor(
    override val priority: Int = PriorityConstant.NORMAL,
    override val interceptFunction: suspend (EventProcessingInterceptor.Context) -> EventProcessingResult,
) : EventProcessingInterceptor,
    SimpleFunctionalEventInterceptor<EventProcessingInterceptor.Context, EventProcessingResult>()


public class SimpleFunctionalEventListenerInterceptor(
    override val point: EventListenerInterceptor.Point,
    override val priority: Int = PriorityConstant.NORMAL,
    override val interceptFunction: suspend (EventListenerInterceptor.Context) -> EventResult,
) : EventListenerInterceptor, SimpleFunctionalEventInterceptor<EventListenerInterceptor.Context, EventResult>()


/**
 * 提供一个 [优先级][priority] 和 [拦截函数][interceptFunction],
 * 得到一个流程拦截器 [EventProcessingInterceptor].
 */
@JvmSynthetic
@SimpleFunctionalProcessingInterceptorDSL
public fun simpleProcessingInterceptor(
    priority: Int = PriorityConstant.NORMAL,
    interceptFunction: suspend (EventProcessingInterceptor.Context) -> EventProcessingResult,
): EventProcessingInterceptor =
    SimpleFunctionalEventProcessingInterceptor(priority = priority, interceptFunction = interceptFunction)

/**
 * 提供一个 [优先级][priority] 和 [拦截函数][interceptFunction],
 * 得到一个流程拦截器 [EventListenerInterceptor].
 */
@JvmSynthetic
@SimpleFunctionalListenerInterceptorDSL
public fun simpleListenerInterceptor(
    point: EventListenerInterceptor.Point = EventListenerInterceptor.Point.DEFAULT,
    priority: Int = PriorityConstant.NORMAL,
    interceptFunction: suspend (EventListenerInterceptor.Context) -> EventResult,
): EventListenerInterceptor =
    SimpleFunctionalEventListenerInterceptor(point = point, priority = priority, interceptFunction = interceptFunction)

////// 4j

/**
 * 提供一个 [优先级][priority] 和 [拦截函数][interceptFunction],
 * 得到一个流程拦截器 [EventProcessingInterceptor].
 */
@Api4J
@JvmOverloads
@JvmName("simpleProcessingInterceptor")
public fun processingInterceptor4J(
    priority: Int = PriorityConstant.NORMAL,
    interceptFunction: Function<EventProcessingInterceptor.Context, EventProcessingResult>,
): EventProcessingInterceptor =
    simpleProcessingInterceptor(priority = priority) { interceptFunction.apply(it) }


/**
 * 提供一个 [优先级][priority] 和 [拦截函数][interceptFunction],
 * 得到一个流程拦截器 [EventListenerInterceptor].
 */
@Api4J
@JvmOverloads
@JvmName("simpleListenerInterceptor")
public fun listenerInterceptor4J(
    point: EventListenerInterceptor.Point = EventListenerInterceptor.Point.DEFAULT,
    priority: Int = PriorityConstant.NORMAL,
    interceptFunction: Function<EventListenerInterceptor.Context, EventResult>,
): EventListenerInterceptor =
    simpleListenerInterceptor(point, priority) { interceptFunction.apply(it) }

