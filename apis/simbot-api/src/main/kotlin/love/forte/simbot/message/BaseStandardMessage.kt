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

package love.forte.simbot.message

import kotlinx.serialization.*
import love.forte.simbot.*
import love.forte.simbot.definition.*
import love.forte.simbot.message.At.Key.equals
import love.forte.simbot.message.At.Key.hashCode
import love.forte.simbot.message.Text.Key.getEmptyText
import love.forte.simbot.message.Text.Key.of
import love.forte.simbot.resources.*

/**
 * 一些由核心提供的标准 [Message] 实例或标准.
 * 标准消息中，仅提供如下实现：
 * - [纯文本消息][Text]
 * - [AT消息][At]
 * - [图片消息][Image]
 * - [表情消息][]
 * - [emoji][]
 *
 */
public sealed interface StandardMessage<out E : Message.Element<E>> : Message.Element<E>

@SerialName("m.std")
@Serializable
public sealed class BaseStandardMessage<out E : Message.Element<E>> : StandardMessage<E>


/** 判断一个 [Message.Element] 是否为一个标准 [Message] 下的实现。 */
public inline val Message.Element<*>.isStandard: Boolean get() = this is StandardMessage


//region Text

/**
 * 纯文本消息。代表一段只存在[文本][text]的消息。
 *
 * @see Text
 */
public interface PlainText<out A : PlainText<A>> : StandardMessage<A> {
    public val text: String

    public companion object Key : Message.Key<PlainText<*>> {
        override fun safeCast(value: Any): PlainText<*>? = safeCast(value)
    }
}

/**
 * 一个文本消息 [Text].
 *
 * 文本消息可以存在多个，但是对于不同平台来讲，有可能存在差异。
 * 部分平台会按照正常的方式顺序排列消息，而有的则会组合消息列表中的所有文本消息为一个整体。
 *
 *
 * @see toText
 * @see Text
 * @see of
 * @see getEmptyText
 */
@Serializable
@SerialName("m.std.text")
public open class Text protected constructor(override val text: String) : PlainText<Text>, BaseStandardMessage<Text>() {
    override val key: Message.Key<Text> get() = Key

    public fun trim(): Text = of(text.trim())

    public operator fun plus(other: Text): Text = when {
        text.isEmpty() -> other
        other.text.isEmpty() -> this
        else -> of(text + other.text)
    }

    public operator fun plus(other: String): Text = if (text.isEmpty()) Text(other) else Text(text + other)
    override fun toString(): String = "Text($text)"
    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is Text) return false
        return text == other.text
    }

    override fun hashCode(): Int = text.hashCode()

    public companion object Key : Message.Key<Text> {
        private val empty = Text("")
        override fun safeCast(value: Any): Text? = doSafeCast(value)

        @JvmStatic
        public fun of(text: String): Text {
            return if (text.isEmpty()) empty
            else Text(text)
        }

        @JvmStatic
        public fun getEmptyText(): Text = empty
    }

}


@Suppress("RemoveRedundantQualifierName")
public fun String.toText(): Text = Text.of(this)

@Suppress("RemoveRedundantQualifierName")
public fun Text(): Text = Text.getEmptyText()
public inline fun Text(block: () -> String): Text = block().toText()
//endregion


//region At
/**
 * 一个 `at` 的标准。
 * at、或者说一个通知信息，用于通知一个用户目标。
 * 一个 At只能代表一个通知目标。
 *
 * @see AtAll
 */
@SerialName("m.std.at")
@Serializable
public data class At @JvmOverloads constructor(
    @Serializable(with = ID.AsCharSequenceIDSerializer::class)
    public val target: ID,
    /**
     * at的类型，默认情况下是针对一个 "用户"(`user`) 的 at。
     *
     * 其他情况，则可能有例如以一个 "角色"(`role`) 等。
     */
    @SerialName("at_type")
    public val atType: String = "user",

    /**
     * 这个at在原始数据中或者原始事件中的样子。默认情况下，是字符串 '@[target]'。
     * 此值将不会参与 [equals] 于 [hashCode] 的计算。
     */
    @SerialName("origin_content")
    public val originContent: String = "@$target"

) : BaseStandardMessage<At>() {
    override val key: Message.Key<At> get() = Key

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is At) return false
        return other.target == target && other.atType == atType
    }


    override fun hashCode(): Int {
        return 31 * (target.hashCode() + atType.hashCode())
    }


    public companion object Key : Message.Key<At> {
        override fun safeCast(value: Any): At? = doSafeCast(value)
    }
}

/**
 * 一个通知所有人的消息。
 */
@SerialName("m.std.atAll")
@Serializable
public object AtAll : BaseStandardMessage<AtAll>(), Message.Key<AtAll> {
    override val key: Message.Key<AtAll>
        get() = this

    override fun toString(): String = "AtAll"
    override fun equals(other: Any?): Boolean = other is AtAll
    override fun safeCast(value: Any): AtAll? = doSafeCast(value)
}

//endregion


//region 图片


/**
 * 一个图片消息。
 *
 * 大多数情况下，图片都是需要上传到平台服务器后再使用的。
 *
 * 因此 [Image] 需要通过 [love.forte.simbot.Bot] 进行上传获取。
 *
 */
public interface Image<E : Image<E>> : StandardMessage<E>, IDContainer {
    /**
     * 上传后的图片会有一个服务端返回的ID。
     *
     * 根据以往的经验，相同图片所上传得到的结果并不100%是相同的。
     *
     */
    override val id: ID


    /**
     * 得到这个图片的数据资源。
     */
    public suspend fun resource(): Resource


    public companion object Key : Message.Key<Image<*>> {
        override fun safeCast(value: Any): Image<*>? = doSafeCast(value)
    }

}


//endregion


//region Emoji
/**
 * 一个 Emoji。
 * 目前绝大多数平台已经不会再用一个独特的 "Emoji" 类型来专门标识Emoji了，
 * 此类型仅作为保留类型。
 *
 * 正常情况下，直接将emoji字符串放在 [文本消息][Text] 中就好了。
 *
 */
@SerialName("m.std.emoji")
@Serializable
public data class Emoji(
    @Serializable(ID.AsCharSequenceIDSerializer::class)
    val id: ID
) : StandardMessage<Emoji> {
    override val key: Message.Key<Emoji>
        get() = Key


    public companion object Key : Message.Key<Emoji> {
        override fun safeCast(value: Any): Emoji? = doSafeCast(value)
    }
}
//endregion


//region face
/**
 * 一个表情。一般代表平台提供的自带表情。
 */
@SerialName("m.std.face")
@Serializable
public data class Face(
    @Serializable(ID.AsCharSequenceIDSerializer::class)
    val id: ID
) : StandardMessage<Face> {
    override val key: Message.Key<Face>
        get() = Key

    public companion object Key : Message.Key<Face> {
        override fun safeCast(value: Any): Face? = doSafeCast(value)
    }
}
//endregion


//region 远程资源
/**
 * [RemoteResource] 代表一个携带 [url] 信息的远程资源。常见为文件或图片等形式。
 *
 * *此类型可能会被删除*
 *
 */
public interface RemoteResource<E : RemoteResource<E>> : StandardMessage<E>, IDContainer {

    /**
     * 对于一个资源，应当又一个对应的唯一ID。
     *
     * 在一些没有的场景下，id可能是 [url] 本身，或是一个固定值。
     */
    override val id: ID

    /**
     * 此资源的URL地址。
     */
    public val url: String

    public companion object Key : Message.Key<RemoteResource<*>> {
        override fun safeCast(value: Any): RemoteResource<*>? = doSafeCast(value)
    }
}
//endregion


//region 多媒体
// /**
//  * 一个多媒体消息。多媒体消息常见为图片、
//  *
//  * 常见的多媒体消息有
//  */
// public interface Multimedia<A : Message.Element<A>> : StandardMessage<A> {
//     public val name: String
// }
//endregion






