/*
 *  Copyright (c) 2021-2022 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  根据 Apache License 2.0 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

package love.forte.simbot.event

import kotlinx.coroutines.runBlocking
import love.forte.simbot.Api4J
import love.forte.simbot.Bot
import love.forte.simbot.ID
import love.forte.simbot.action.DeleteSupport
import love.forte.simbot.action.SendSupport
import love.forte.simbot.definition.*
import love.forte.simbot.message.ReceivedMessageContent
import love.forte.simbot.message.RemoteMessageContainer
import love.forte.simbot.message.doSafeCast


/**
 * 一个存在消息内容的[事件][Event]。
 *
 * @see ContactMessageEvent
 * @see ChatroomMessageEvent
 *
 */
public interface MessageEvent : Event, RemoteMessageContainer {
    override val bot: Bot
    override val metadata: Event.Metadata


    /**
     * 当前消息事件所对应的事件源头.
     *
     * 通常情况下，[source] 都是可以 [发送消息][SendSupport] 的。
     *
     */
    public suspend fun source(): Objectives


    @Api4J
    public val source: Objectives
        get() = runBlocking { source() }


    /**
     * 当前消息事件的消息正文。
     */
    override val messageContent: ReceivedMessageContent


    // Event key
    public companion object Key : BaseEventKey<MessageEvent>("api.message") {
        override fun safeCast(value: Any): MessageEvent? = doSafeCast(value)
    }
}


/**
 * 来自一个[联系人][Contact]消息消息事件。 这通常代表一个私聊消息事件。
 *
 */
public interface ContactMessageEvent : MessageEvent, UserEvent {


    /**
     * 消息的信息来源是一个可以进行信息交互的 [联系人][Contact]
     */
    override suspend fun user(): Contact

    @Api4J
    override val user: Contact
        get() = runBlocking { user() }


    override suspend fun source(): Contact = user()

    @Api4J
    override val source: Contact
        get() = runBlocking { source() }


    /**
     * 通常情况下，联系人消息的可见性是私人的。
     */
    override val visibleScope: Event.VisibleScope
        get() = Event.VisibleScope.PRIVATE

    public companion object Key : BaseEventKey<ContactMessageEvent>(
        "api.contact_message", MessageEvent, UserEvent
    ) {
        override fun safeCast(value: Any): ContactMessageEvent? = doSafeCast(value)
    }
}


/**
 * 一个来自于[好友][Friend]的消息事件。这通常代表为一个私聊消息事件。
 *
 * @see ContactMessageEvent
 */
public interface FriendMessageEvent : ContactMessageEvent, FriendEvent {

    /**
     * 消息的信息来源是一个可以进行信息交互的 [好友][Friend]
     */
    override suspend fun friend(): Friend


    override suspend fun user(): Friend = friend()

    @Api4J
    override val user: Friend
        get() = runBlocking { user() }


    override suspend fun source(): Friend = user()

    @Api4J
    override val source: Friend
        get() = runBlocking { source() }


    /**
     * 通常情况下，联系人消息的可见性是私人的。
     */
    override val visibleScope: Event.VisibleScope
        get() = Event.VisibleScope.PRIVATE


    public companion object Key : BaseEventKey<FriendMessageEvent>(
        "api.friend_message", ContactMessageEvent, FriendEvent
    ) {
        override fun safeCast(value: Any): FriendMessageEvent? = doSafeCast(value)
    }
}


/**
 * 一个来自聊天室的消息事件。
 *
 *
 * @see GroupMessageEvent
 * @see ChannelMessageEvent
 *
 */
public interface ChatroomMessageEvent : MessageEvent, OrganizationEvent, DeleteSupport, RemoteMessageContainer {

    /**
     * 来自的聊天室，通常是一个群或者一个频道。
     */
    override suspend fun source(): ChatRoom

    @Api4J
    override val source: ChatRoom
        get() = runBlocking { source() }

    /**
     * 这个消息的发送者.
     */
    public suspend fun author(): Member

    @Api4J
    public val author: Member
        get() = runBlocking { author() }


    /**
     * 预期内，假若当前bot拥有足够的权限则可以对消息进行删除（撤回）操作。
     *
     */
    override suspend fun delete(): Boolean


    public companion object Key : BaseEventKey<ChatroomMessageEvent>(
        "api.privateMessage", MessageEvent.Key
    ) {
        override fun safeCast(value: Any): ChatroomMessageEvent? = doSafeCast(value)
    }

}


/**
 *  代表一个来自[群][Group]的消息事件。
 *
 */
public interface GroupMessageEvent : ChatroomMessageEvent, GroupEvent {

    /**
     * 消息来自的群。
     */
    override suspend fun group(): Group

    @Api4J
    override val group: Group
        get() = runBlocking { group() }


    override suspend fun source(): Group = group()


    @Api4J
    override val source: Group
        get() = runBlocking { source() }

    /**
     * 消息发送者
     */
    override suspend fun author(): Member

    @Api4J
    override val author: Member
        get() = runBlocking { author() }


    public companion object Key : BaseEventKey<GroupMessageEvent>(
        "api.group_message", ChatroomMessageEvent, GroupEvent
    ) {
        override fun safeCast(value: Any): GroupMessageEvent? = doSafeCast(value)
    }

}

/**
 *
 * 代表一个来自[频道][Channel]的消息事件。
 *
 */
public interface ChannelMessageEvent : ChatroomMessageEvent, ChannelEvent {
    /**
     * 消息来自的频道
     */
    override suspend fun channel(): Channel

    @Api4J
    override val channel: Channel
        get() = runBlocking { channel() }


    override suspend fun source(): Channel = channel()

    @Api4J
    override val source: Channel
        get() = runBlocking { source() }

    override suspend fun author(): Member

    @Api4J
    override val author: Member
        get() = runBlocking { author() }


    public companion object Key : BaseEventKey<ChannelMessageEvent>(
        "api.channel_message", ChatroomMessageEvent, ChannelEvent
    ) {
        override fun safeCast(value: Any): ChannelMessageEvent? = doSafeCast(value)
    }
}


/**
 * 消息被回应事件。
 *
 * 消息被回应事件的内容十分不稳定，因此标准接口中仅提供最低限度的ID相关内容。
 *
 */
public interface MessageReactedEvent : MessageEvent {
    /**
     * 被回应的消息的ID。
     *
     * 事件定义无法保证被回应的 [消息][love.forte.simbot.message.MessageContent] 能够支持被获取，
     * 但是对于此事件，应当至少存在一个 被回应消息的ID
     */
    public val reactedId: ID

    /**
     * 回应这条消息的用户ID。
     */
    public val reactorId: ID

}
