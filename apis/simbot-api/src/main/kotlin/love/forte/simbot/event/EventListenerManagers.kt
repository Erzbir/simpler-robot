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

package love.forte.simbot.event

import love.forte.simbot.*


/**
 *
 * 监听函数容器。
 *
 * @author ForteScarlet
 */
public interface EventListenerContainer {

    /**
     * 通过一个ID得到一个当前监听函数下的对应函数。
     */
    public operator fun get(id: ID): EventListener?

}


/**
 * 监听函数注册器
 */
public interface EventListenerRegistrar {

    /**
     * 注册一个监听函数。
     *
     * 注册监听函数并不一定是原子的，可能会存在注册结果的滞后性（注册不一定会立即生效）。
     *
     * ## 滞后性
     * 例如你在时间点A注册一个监听函数，而时间点B（=时间点A + 10ms）时推送了一个事件，
     *
     * 这时候无法保证在时间点A之后的这个事件能够触发此监听函数。
     *
     * ## 脆弱的
     * [register] 被标记为 [脆弱的API][FragileSimbotApi], 因为 [EventListenerRegistrar] 的行为是在 [EventListenerManager] 构建完成后的运行时期动态添加**额外的**监听函数。
     *
     * 在 [EventListenerManager] 中（以核心实现 [love.forte.simbot.core.event.CoreListenerManager]为例），会针对所有监听函数并根据他们所监听的事件类型进行解析与缓存，以改善提升每次事件触发时的响应速度。
     * 也因由此，对监听函数的解析与缓存所代来的问题就是会在运行时额外添加的时候带来更大的性能损耗和更长的不一致延迟 —— 它会在收到注册请求后，清除所有的内部缓存并等待下一次的缓存构建。而这个行为会仅仅因为一次监听函数的注册而波及到**所有**的监听函数。
     *
     * 相关的动态追加*也许*未来会进行更多的优化，也有可能会将其废弃，但是当下节点，请尽量避免。
     *
     *
     * @throws IllegalStateException 如果出现ID重复
     */
    @FragileSimbotApi
    public fun register(listener: EventListener)

}


/**
 * 事件监听器管理器标准接口。
 *
 *
 */
public interface EventListenerManager :
    EventProcessor, EventListenerContainer, EventListenerRegistrar {

    /**
     * 获取当前监听函数管理器中所使用的全局作用域的context。
     * 其获取结果相当于在监听函数中使用 [EventProcessingContext.Scope.Global] 进行获取而得到的作用域。
     *
     * 全局作用域上下文与每一个独立的事件处理流程无关，理应能够直接通过当前 [EventListenerManager] 进行获取。
     *
     */
    @ExperimentalSimbotApi
    public val globalScopeContext: ScopeContext


    /**
     * 获取当前监听函数管理器中所使用的持续会话context。
     * 其获取结果相当于在监听函数中使用 [EventProcessingContext.Scope.ContinuousSession] 进行获取而得到的作用域。
     *
     * 持续会话上下文与每一个独立的事件流程无关，其生命周期应该与当前监听函数管理器有关，理应能够直接通过当前 [EventListenerManager] 进行获取以便在监听流程之外进行会话触发。
     *
     * ```kotlin
     * fun process(listenerManager: EventListenerManager) {
     *      // 取得一个provider
     *      val provider = listenerManager.continuousSessionContext.getProvider<Int>(123.ID)
     *      provider?.push(666)
     * }
     * ```
     *
     */
    @ExperimentalSimbotApi
    public val continuousSessionContext: ContinuousSessionContext


}


/**
 * 用于配置 [EventListenerManager] 的基础抽象类，其定义了针对组件的注册api。
 */
public interface EventListenerManagerConfiguration {

    /**
     * 注册一个组件信息到当前事件管理器中。
     *
     * @param registrar 组件注册器。
     * @param config 配置函数
     */
    public fun <C : Component, Config : Any> install(
        registrar: ComponentRegistrar<C, Config>,
        config: Config.() -> Unit = {}
    )


    /**
     * 尝试注册所有可寻的组件到当前配置中。
     *
     * 此函数需要对应的组件注册器支持 `Java SPI` 加载。
     *
     * @see ComponentRegistrar
     */
    @ExperimentalSimbotApi
    public fun installAll()


}