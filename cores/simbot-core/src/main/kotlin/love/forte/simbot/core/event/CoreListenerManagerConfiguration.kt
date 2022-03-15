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

package love.forte.simbot.core.event

import kotlinx.coroutines.*
import love.forte.simbot.*
import love.forte.simbot.event.*
import love.forte.simbot.event.EventListener
import love.forte.simbot.utils.*
import java.util.*
import java.util.function.Function
import kotlin.coroutines.*


@DslMarker
internal annotation class CoreEventManagerConfigDSL

/**
 * [CoreListenerManager] 的配置文件.
 * 当配置文件作为构建参数的时候，他会被立即使用。
 *
 * ### 拦截器
 * 通过 [interceptors] [addListenerInterceptors] [addListenerInterceptors] 等相关函数来向配置中追加对应作用域的拦截器。
 * ```kotlin
 * coreListenerManager {
 *
 * }
 * ```
 * ### 监听函数
 *
 *
 * @see CoreListenerManager.newInstance
 * @see coreListenerManager
 */
@CoreEventManagerConfigDSL
public class CoreListenerManagerConfiguration : EventListenerManagerConfiguration {
    private val componentConfigurations = mutableMapOf<Attribute<*>, Any.() -> Unit>()
    private val componentRegistrars = mutableMapOf<Attribute<*>, () -> Component>()

    @CoreEventManagerConfigDSL
    override fun <C : Component, Config : Any> install(registrar: ComponentRegistrar<C, Config>, config: Config.() -> Unit) {
        val key = registrar.key
        val oldConfig = componentConfigurations[key]

        componentConfigurations[key] = {
            // 追加配置
            oldConfig?.invoke(this)
            @Suppress("UNCHECKED_CAST")
            (this as Config).config()
        }

        if (key in componentRegistrars) return

        componentRegistrars[key] = {
            val configuration = componentConfigurations[key]!!
            registrar.register(configuration)
        }
    }

    /**
     * 尝试加载所有的 [ComponentRegistrarFactory] 并注册它们。统一使用默认配置进行注册。
     */
    @ExperimentalSimbotApi
    @CoreEventManagerConfigDSL
    override fun installAll() {
        val factories = ServiceLoader.load(ComponentRegistrarFactory::class.java, this.currentClassLoader)
        factories.forEach {
            install(it.registrar)
        }
    }

    /**
     * 事件管理器的上下文. 可以基于此提供调度器。
     * 但是 [CoreListenerManager] 并不是一个作用域，因此不可以提供 `Job`.
     */
    @CoreEventManagerConfigDSL
    public var coroutineContext: CoroutineContext = EmptyCoroutineContext


    private var processingInterceptors = mutableIDMapOf<EventProcessingInterceptor>()


    private var listenerInterceptors = mutableIDMapOf<EventListenerInterceptor>()

    // 初始监听函数
    private var listeners = mutableListOf<EventListener>()

    /**
     * 自定义的监听函数异常处理器。
     *
     */
    @Volatile
    @JvmSynthetic
    public var listenerExceptionHandler: ((Throwable) -> EventResult)? = null


    @JvmSynthetic
    @CoreEventManagerConfigDSL
    public fun listenerExceptionHandler(handler: (Throwable) -> EventResult): CoreListenerManagerConfiguration = also {
        listenerExceptionHandler = handler
    }

    @Api4J
    @CoreEventManagerConfigDSL
    public fun listenerExceptionHandler(handler: Function<Throwable, EventResult>): CoreListenerManagerConfiguration =
        also {
            listenerExceptionHandler = handler::apply
        }

    //region 拦截器相关
    /**
     * 添加一个流程拦截器，ID需要唯一。
     * 如果出现重复ID，会抛出 [IllegalStateException] 并且不会真正的向当前配置中追加数据。
     *
     * @throws IllegalStateException 如果出现重复ID
     */
    @Synchronized
    @CoreEventManagerConfigDSL
    public fun addProcessingInterceptors(interceptors: Map<ID, EventProcessingInterceptor>): CoreListenerManagerConfiguration =
        also {
            if (interceptors.isEmpty()) return this

            val processingInterceptorsCopy = processingInterceptors.toMutableMap()

            for ((id, interceptor) in interceptors) {
                processingInterceptorsCopy.merge(id, interceptor) { _, _ ->
                    throw IllegalStateException("Duplicate ID: $id")
                }
            }
            processingInterceptors = mutableIDMapOf(processingInterceptorsCopy)
        }

    /**
     * 添加一个流程拦截器，ID需要唯一。
     * 如果出现重复ID，会抛出 [IllegalStateException] 并且不会真正的向当前配置中追加数据。
     *
     * @throws IllegalStateException 如果出现重复ID
     */
    @Synchronized
    @CoreEventManagerConfigDSL
    public fun addListenerInterceptors(interceptors: Map<ID, EventListenerInterceptor>): CoreListenerManagerConfiguration =
        also {
            if (interceptors.isEmpty()) return this

            val listenerInterceptorsCopy = listenerInterceptors.toMutableMap()

            for ((id, interceptor) in interceptors) {
                listenerInterceptorsCopy.merge(id, interceptor) { _, _ ->
                    throw IllegalStateException("Duplicate ID: $id")
                }
            }

            listenerInterceptors = mutableIDMapOf(listenerInterceptorsCopy)
        }

    /**
     * 进入到拦截器配置域。
     * ```kotlin
     * interceptors {
     *    processingIntercept { ... }
     *    listenerIntercept { ... }
     * }
     * ```
     */
    @CoreEventManagerConfigDSL
    public fun interceptors(block: EventInterceptorsGenerator.() -> Unit): CoreListenerManagerConfiguration =
        interceptors().also(block).end()

    /**
     * 进入到拦截器配置域。
     *
     * *此函数更适合在Java中进行链式调用*。
     * ```java
     * configuration
     *         .interceptors() // enter to interceptors generator
     *         .listenerIntercept(context -> {
     *             // do something
     *             return context.proceedBlocking();
     *         })
     *         .processingIntercept(context -> {
     *             // do something
     *             return context.proceedBlocking();
     *         })
     *         .end() // back to config
     *         .addListener(...);
     *
     * ```
     */
    @Suppress("MemberVisibilityCanBePrivate")
    public fun interceptors(): EventInterceptorsGenerator = EventInterceptorsGenerator { ps, ls ->
        addProcessingInterceptors(ps)
        addListenerInterceptors(ls)
        this
    }

    //endregion


    //region 监听函数相关

    /**
     * 直接添加一个监听函数。
     * ```kotlin
     * addListener(coreListener(FriendMessageEvent) { context, event ->
     *     delay(200)
     *     event.friend().send("Hi! context: $context")
     * })
     * ```
     */
    public fun addListener(listener: EventListener): CoreListenerManagerConfiguration = also {
        listeners.add(listener)
    }

    /**
     * 添加多个监听函数。
     */
    public fun addListeners(listeners: Collection<EventListener>): CoreListenerManagerConfiguration = also {
        this.listeners.addAll(listeners)
    }

    /**
     * 添加多个监听函数。
     */
    public fun addListeners(vararg listeners: EventListener): CoreListenerManagerConfiguration = also {
        this.listeners.addAll(listeners)
    }

    /**
     * 进入到拦截器配置域.
     *
     *  @see EventListenersGenerator
     */
    @EventListenersGeneratorDSL
    public fun listeners(block: EventListenersGenerator.() -> Unit): CoreListenerManagerConfiguration =
        listeners().also(block).end()


    /**
     * 进入到监听函数配置域。
     *
     * *此函数更适合在Java中进行链式调用*。
     *
     *
     */
    @Suppress("MemberVisibilityCanBePrivate")
    public fun listeners(): EventListenersGenerator = EventListenersGenerator {
        this.listeners.addAll(it)
        this
    }

    //endregion


    /**
     * 事件流程上下文的处理器。
     */
    // 暂时不公开
    // @CoreEventManagerConfigDSL
    public var eventProcessingContextResolver: (manager: CoreListenerManager, scope: CoroutineScope) -> EventProcessingContextResolver<*> =
        { _, scope -> CoreEventProcessingContextResolver(scope) }
        private set


    internal fun build(): ConfigResult {
        // install components
        val components = componentRegistrars.values.associate {
            val comp = it()
            comp.id.literal to comp
        }

        return ConfigResult(
            coroutineContext,
            components = components,
            exceptionHandler = listenerExceptionHandler,
            processingInterceptors = idMapOf(processingInterceptors),
            listenerInterceptors = idMapOf(listenerInterceptors),
            listeners = listeners.toList()
        )
    }
}


internal data class ConfigResult(
    internal val coroutineContext: CoroutineContext,
    internal val components: Map<String, Component>,
    internal val exceptionHandler: ((Throwable) -> EventResult)? = null,
    internal val processingInterceptors: IDMaps<EventProcessingInterceptor>,
    internal val listenerInterceptors: IDMaps<EventListenerInterceptor>,
    internal val listeners: List<EventListener>

)

/*
 internal var processingInterceptors = mutableIDMapOf<EventProcessingInterceptor>()


    internal var listenerInterceptors = mutableIDMapOf<EventListenerInterceptor>()

    // 初始监听函数
    internal var listeners = mutableListOf<EventListener>()
 */