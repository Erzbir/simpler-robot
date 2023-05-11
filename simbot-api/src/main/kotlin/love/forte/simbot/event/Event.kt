/*
 * Copyright (c) 2021-2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */


package love.forte.simbot.event

import love.forte.simbot.*
import love.forte.simbot.bot.Bot
import love.forte.simbot.definition.BotContainer
import love.forte.simbot.definition.IDContainer
import love.forte.simbot.event.Event.Key.Companion.getKey
import love.forte.simbot.event.Event.Key.Companion.isSub
import love.forte.simbot.message.doSafeCast
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import kotlin.reflect.full.companionObjectInstance
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.safeCast

/**
 * ## 事件类型
 *
 * 所有 [事件][Event] 的顶层接口。
 *
 * ## Key
 * 在事件处理的时候，将不会根据其 `class` 进行直接的类型关系判断来决定事件监听，而是根据 [Event.key][Event.Key] 进行事件之间的继承关系的判断。
 * 对于任意一个继承此接口的事件类型（包括其他接口或抽象），
 * 其类型中必须存在一个实现了 [Event.Key] 的伴生对象或者通过 [EventKey] 注解指定 [Event.Key] 的实现，否则此事件将会被视为 **不可监听**。
 *
 *
 * ## 泛型事件类型
 *
 * 所有能够监听的事件中，**不建议**监听带有泛型信息的事件类型（例如 [ChangedEvent]）,
 * 虽然它们允许被监听，但是它们大多数都代表对其他事件的类型约束。
 *
 * 并且，在进行事件监听的时候，事件类型的判断 **不支持** 泛型判断，因此如果你需要监听这些携带泛型的事件类型，
 * 那么你必须使用kotlin中的 `*`，在Java中使用 `?` 或直接忽略它。否则会很容易导致出现异常。
 *
 *
 *
 *
 * @see Event.Key
 * @author ForteScarlet
 */
public interface Event : BotContainer, IDContainer, ComponentContainer {

    /**
     * 事件的唯一标识。
     */
    override val id: ID

    /**
     * 与这个事件有关系的 [Bot].
     */
    override val bot: Bot

    /**
     * 一个事件所属的组件。
     * 通常与 [bot] 的组件所属一致。
     */
    override val component: Component
        get() = bot.component

    /**
     * 此时间发生的时间戳。
     *
     * 如果相关组件支持，则为对应时间，如果不支持则一般为构建时的瞬时时间戳。
     */
    public val timestamp: Timestamp

    /**
     * 得到当前事件所对应的类型key。
     */
    public val key: Key<out Event>

    /**
     * 所有事件的根类型。
     */
    public companion object Root : Key<Event> {

        @Suppress("MemberVisibilityCanBePrivate")
        public const val ID_VALUE: String = "api.root"

        /**
         * Event根节点的唯一ID。
         */
        override val id: CharSequenceID = ID_VALUE.ID

        /**
         * Event是所有事件的根，不可能是其他事件的子项.
         */
        override val parents: Set<Key<*>> get() = emptySet()


        override fun safeCast(value: Any): Event? = doSafeCast<Event>(value)

        override fun toString(): String {
            return "RootEvent(id=$ID_VALUE)"
        }
    }

    /**
     * 一个事件类型的Key。所有的计划对外的事件类型都必须通过 **伴生对象** 实现此类型并提供一个事件**唯一**的ID名称。
     * 并非所有的事件接口 [Event] 的类型都能够允许被监听，有些事件类型可能仅用做标记或者由于其他原因无法/不允许被**直接**监听，
     * 因此在事件调度中，需要通过 [Key] 来判断事件类型与其之间的继承关系。
     *
     * 所有事件的 [Key.id] 必须尽可能保证唯一，因此建议对ID进行命名的时候使用较为特殊的命名方式以杜绝出现ID重复。
     * id重复不一定会出现异常提示，但是在使用 [isSub] 等方法的时候，很有可能会出现缓存内容混乱进而导致引发预期外的异常。
     *
     * 事件类型可以继承，且允许多继承，实现方可以通过 [isSub] 来判断当前事件是否为某个类型的子类型。
     *
     * 比如
     * ```kotlin
     * val event: MessageEvent = ...
     * val isSub: Boolean = MessageEvent.Key isSub Event.Key
     * ```
     * [Key] 的继承关系是单向传递的，因此你能够通过一个key找到它继承的所有父类型，但是无法反向查找。

     *
     * 当一个事件提供伴生Key的时候，[E] 建议且应当与当前事件类型**一致**, 因为在 [Key.getKey] 等通过类型获取Key的场景下，均默认为 [Key] 的类型与当前事件类型一致。
     * 如下所示：
     * ```kotlin
     * interface MyEvent : Event {
     *      companion object Key: Event.Key<MyEvent> {
     *          // ...
     *      }
     * }
     * ```
     *
     *
     *
     * @see getKey
     * @see EventKey
     * @see isSub
     */
    public interface Key<E : Event> {
        /**
         * 此事件的ID，需要是唯一的。假若在事件注册时出现了ID相同但不是同一个Key的情况将会导致异常。
         */
        public val id: CharSequenceID

        /**
         * 此事件所继承的所有父事件。
         * 此属性应当是不可变的，不应在运行期内发生变更。
         */
        public val parents: Set<Key<*>>

        /**
         * 将一个提供的类型转化为当前的目标事件。
         * 如果得到null，则说明无法被转化。
         */
        public fun safeCast(value: Any): E?


        public companion object {
            private val keyCache = ConcurrentHashMap<KClass<*>, Key<*>>()
            private val subCache = ConcurrentHashMap<String, ConcurrentHashMap<String, Unit>>()
            private val notSubCache = ConcurrentHashMap<String, ConcurrentHashMap<String, Unit>>()
    
            private fun getSubCache(id: String): ConcurrentHashMap<String, Unit> = subCache.computeIfAbsent(id) { ConcurrentHashMap() }
            private fun getNotSubCache(id: String): ConcurrentHashMap<String, Unit> = notSubCache.computeIfAbsent(id) { ConcurrentHashMap() }
            
            /**
             * 检测当前接收器是否为 [from] 的子类型。
             */
            @JvmStatic
            public infix fun Key<*>.isSub(from: Key<*>): Boolean {
                val target = this
                if (from === Event) return true
                if (from == target) return true
                if (from in target.parents) return true

                val tid = target.id.literal
                val fid = from.id.literal
                if (getSubCache(tid).containsKey(fid)) {
                    return true
                }
                if (getNotSubCache(tid).containsKey(fid)) {
                    return false
                }


                val isSub = target.parents.any {
                    it isSub from
                }
                if (isSub) {
                    getSubCache(tid)[fid] = Unit
                } else {
                    getNotSubCache(tid)[fid] = Unit
                }
                return isSub
            }

            /**
             * 尝试通过一个 [Event] 的 [KClass] 来得到一个其对应的 [Key].
             */
            @JvmSynthetic
            @OptIn(Api4J::class)
            public fun <E : Event> getKey(type: KClass<E>): Key<E> {
                val cached = keyCache[type]
                @Suppress("UNCHECKED_CAST")
                if (cached != null) return cached as Key<E>

                // companion try
                val companionObject = type.companionObjectInstance

                @Suppress("UNCHECKED_CAST")
                if (companionObject != null && companionObject is Key<*>) return companionObject as Key<E>



                @Suppress("UNCHECKED_CAST")
                return keyCache.computeIfAbsent(type) { k ->
                    // find EventKey annotation
                    k.findAnnotation<EventKey>()?.toKey<E>()
                        ?: throw NoSuchEventKeyDefineException("Unable to find event key in [$type] by companion object or @EventKey annotation")
                } as Key<E>
            }

            @Api4J
            public fun <T : Event> getKey(type: Class<T>): Key<T> = getKey(type.kotlin)


            @JvmSynthetic
            public inline fun <reified T : Event> getKey(): Key<T> = getKey(T::class)

        }

    }
}


/**
 *
 * 通过注解标记一个 [Event] 类型所对应的 [Event.Key] 数据.
 *
 * 用于在无法实现伴生对象的情况下（例如Java）提供 [Event.Key] 信息.
 *
 * 此注解没有"继承"的特性，不可嵌套。
 *
 * 使用方式如：
 * ```java
 *  @EventKey(id = "example.test_event", type = MyTestEvent4J.class, parents = { MessageEvent.class, MessageEvent.class })
 *  public interface MyTestEvent4J extends MessageEvent, ChannelEvent {
 *  }
 * ```
 *
 * @param id 此事件的 [Event.Key.id]
 * @param type 被标记事件的类型
 * @param parents 此事件的 [Event.Key.parents]
 *
 * @see Event.Key
 */
@Api4J
@Target(AnnotationTarget.CLASS)
@MustBeDocumented
public annotation class EventKey(
    val id: String,
    val type: KClass<out Event>,
    val parents: Array<KClass<out Event>>,
)

/**
 * [EventKey] to [Event.Key].
 *
 */
@Suppress("UNCHECKED_CAST", "RemoveRedundantQualifierName")
@OptIn(Api4J::class)
private fun <T : Event> EventKey.toKey(): Event.Key<T> =
    AnnotationEventKey(
        id,
        type as KClass<T>, //
        parents.mapNotNull { it.takeIf { t -> t != type }?.let { t -> Event.Key.getKey(t) } }.toSet()
    )


private class AnnotationEventKey<T : Event>(
    idValue: String,
    private val type: KClass<T>,
    override val parents: Set<Event.Key<*>>,
) : Event.Key<T> {
    override val id: CharSequenceID = idValue.ID
    override fun safeCast(value: Any): T? = type.safeCast(value)
    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is Event.Key<*>) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}


/**
 * 通过 [KClass] 获取其对应的 [Event.Key].
 */
@JvmSynthetic
@Suppress("RemoveRedundantQualifierName")
public fun <T : Event> KClass<T>.getKey(): Event.Key<T> = Event.Key.getKey(this)

/**
 * 判断当前类型是否为提供类型的子类型。
 *
 */
public operator fun Event.Key<*>.contains(parentIdMaybe: String): Boolean {
    if (id.literal == parentIdMaybe) return true
    return parents.any { parents -> parentIdMaybe in parents }
}

/**
 * 判断当前类型是否为提供类型的子类型。
 *
 */
public operator fun Event.Key<*>.contains(parentIdMaybe: ID): Boolean {
    if (id == parentIdMaybe) return true
    return parents.any { parents -> parentIdMaybe in parents }
}

/**
 * [Event.Key] 的基础抽象类，当一个事件仅来自于一个父级事件的时候可以使用此抽象类。
 */
public abstract class BaseEventKey<E : Event>(
    idValue: String,
    override val parents: Set<Event.Key<*>> = emptySet(),
) : Event.Key<E> {
    public constructor(idValue: String, vararg parents: Event.Key<*>) : this(idValue, parents.toSet())

    override val id: CharSequenceID = idValue.ID
    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is Event.Key<*>) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
    override fun toString(): String = "EventKey(id=$id)"
}


/////


/**
 * 没有定义 [Event.Key] 异常。
 *
 */
public class NoSuchEventKeyDefineException internal constructor(message: String?) : SimbotIllegalStateException(message)
