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
 *
 */

package love.forte.simboot.core.listener

import love.forte.simboot.listener.*
import love.forte.simbot.*
import love.forte.simbot.event.*
import kotlin.reflect.*
import kotlin.reflect.full.*


/**
 * 如果参数是 [Event]、[EventListenerProcessingContext]、[EventListener] 类型, 检测并通过 [EventListenerProcessingContext] 中提供的内容进行提供。
 *
 */
public object EventParameterBinderFactory : ParameterBinderFactory {

    @OptIn(ExperimentalSimbotApi::class)
    override fun resolveToBinder(context: ParameterBinderFactory.Context): ParameterBinderResult {
        val type = context.parameter.type
        val classifier = type.classifier as? KClass<*> ?: return ParameterBinderResult.empty()
        //if (classifier !is KClass<*>)

        val nullable = type.isMarkedNullable

        return when {
            // 是不是 Event子类型
            classifier.isSubclassOf(Event::class) -> {
                @Suppress("UNCHECKED_CAST")
                return ParameterBinderResult.normal(EventInstanceBinder(classifier as KClass<Event>))
            }

            // 是不是 EventListener 子类型
            classifier.isSubclassOf(EventListener::class) -> {
                @Suppress("UNCHECKED_CAST")
                return ParameterBinderResult.normal(EventListenerInstanceBinder(classifier as KClass<EventListener>))
            }

            // 是不是 EventProcessingContext 子类型
            classifier.isSubclassOf(EventProcessingContext::class) -> {
                @Suppress("UNCHECKED_CAST")
                return ParameterBinderResult.normal(EventListenerProcessingContextInstanceBinder(classifier as KClass<EventProcessingContext>))
            }

            // Scope 相关类型

            // classifier.isSubclassOf(EventProcessingContext.Scope.Instant.type) -> {
            classifier.isSubclassOf(InstantScopeContext::class) -> {
                return ParameterBinderResult.normal(attributeBinder(nullable, EventProcessingContext.Scope.Instant) { "Scope [Instant] in current context is null." } )
            }
            classifier.isSubclassOf(GlobalScopeContext::class) -> {
                return ParameterBinderResult.normal(attributeBinder(nullable, EventProcessingContext.Scope.Global) { "Scope [Global] in current context is null." } )
            }


            classifier.isSubclassOf(ContinuousSessionContext::class) -> {
                return ParameterBinderResult.normal(attributeBinder(nullable, EventProcessingContext.Scope.ContinuousSession) { "Scope [ContinuousSession] in current context is null." } )
            }


            else -> ParameterBinderResult.empty()
        }

    }
}

/**
 * 提供事件本体作为参数。
 */
private class EventInstanceBinder(private val targetType: KClass<Event>) : ParameterBinder {
    override suspend fun arg(context: EventListenerProcessingContext): Result<Any?> {
        // 如果当前事件类型是目标类型的子类，提供参数
        val event = context.event
        return if (targetType.isInstance(event)) Result.success(event)
        else Result.failure(BindException("The type of event is inconsistent with the target type $targetType"))
    }
}

/**
 * 提供 [EventListenerProcessingContext] 作为参数。
 */
private class EventListenerProcessingContextInstanceBinder(private val targetType: KClass<EventProcessingContext>) :
    ParameterBinder {
    override suspend fun arg(context: EventListenerProcessingContext): Result<Any?> {
        // 如果当前context类型是目标类型的子类，提供参数
        return if (targetType.isInstance(context)) Result.success(context)
        else Result.failure(BindException("The type of context is inconsistent with the target type $targetType"))
    }
}

/**
 * 提供监听函数自身作为参数。
 */
private class EventListenerInstanceBinder(private val targetType: KClass<EventListener>) : ParameterBinder {
    override suspend fun arg(context: EventListenerProcessingContext): Result<Any?> {
        // 如果当前listener类型是目标类型的子类，提供参数
        val listener = context.listener
        return if (targetType.isInstance(listener)) Result.success(listener)
        else Result.failure(BindException("The type of listener is inconsistent with the target type $targetType"))
    }
}

private inline fun attributeBinder(nullable: Boolean, attribute: Attribute<*>, nullMessageBlock: () -> String): AttributeBinder {
    return if (nullable) AttributeBinder.Nullable(attribute)
    else AttributeBinder.Notnull(attribute, nullMessageBlock())
}

private sealed class AttributeBinder : ParameterBinder {

    protected abstract val attribute: Attribute<*>

    class Nullable(override val attribute: Attribute<*>) : AttributeBinder() {
        override suspend fun arg(context: EventListenerProcessingContext): Result<Any?> {
            return context.getAttribute(attribute).let { Result.success(it) }
        }
    }

    class Notnull(override val attribute: Attribute<*>, private val nullMessage: String) : AttributeBinder() {
        override suspend fun arg(context: EventListenerProcessingContext): Result<Any?> {
            return context.getAttribute(attribute)?.let { Result.success(it) }
                ?: Result.failure(NullPointerException(nullMessage))
        }
    }
}
