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

package love.forte.simbot.event

import love.forte.plugin.suspendtrans.annotation.JvmAsync
import love.forte.plugin.suspendtrans.annotation.JvmBlocking
import love.forte.simbot.bot.Bot
import love.forte.simbot.definition.*
import love.forte.simbot.message.doSafeCast


/**
 * 一个与 [Objective] 相关的事件。
 * 可能是与联系人（好友、成员等）或者与组织（群、频道等）相关的事件。
 *
 * [ObjectiveEvent] 是一个标记用事件类型，不存在 Key, 不允许被直接监听。
 */
@BaseEvent
public sealed interface ObjectiveEvent : Event {
    override val bot: Bot
}


// region user events
/**
 * 一个与 [用户][User] 相关的事件。
 */
@BaseEvent
public interface UserEvent : ObjectiveEvent, UserInfoContainer {
    /**
     * 这个[用户][User]。
     */
    @JvmBlocking(asProperty = true, suffix = "")
    @JvmAsync(asProperty = true, suffix = "")
    override suspend fun user(): User
    
    public companion object Key : BaseEventKey<UserEvent>("api.user") {
        override fun safeCast(value: Any): UserEvent? = doSafeCast(value)
    }
    
}


public suspend inline fun <R> UserEvent.inUser(block: User.() -> R): R = user().let(block)
public suspend inline fun <R> UserEvent.useUser(block: (User) -> R): R = user().let(block)


/**
 * 一个与 [成员][Member] 相关的事件。
 */
@BaseEvent
public interface MemberEvent : UserEvent, MemberInfoContainer {
    /**
     * 这个[成员][Member]
     */
    @JvmBlocking(asProperty = true, suffix = "")
    @JvmAsync(asProperty = true, suffix = "")
    override suspend fun member(): Member
    
    
    /**
     * 这个[成员][Member]
     */
    @JvmBlocking(asProperty = true, suffix = "")
    @JvmAsync(asProperty = true, suffix = "")
    override suspend fun user(): Member
    
    
    public companion object Key : BaseEventKey<MemberEvent>("api.member", UserEvent) {
        override fun safeCast(value: Any): MemberEvent? = doSafeCast(value)
    }
}


public suspend inline fun <R> MemberEvent.inMember(block: Member.() -> R): R = member().let(block)
public suspend inline fun <R> MemberEvent.useMember(block: (Member) -> R): R = member().let(block)


/**
 * 一个与 [好友][Friend] 相关的事件。
 */
@BaseEvent
public interface FriendEvent : UserEvent, FriendInfoContainer {
    /**
     * 这个[好友][Friend]
     */
    @JvmBlocking(asProperty = true, suffix = "")
    @JvmAsync(asProperty = true, suffix = "")
    override suspend fun friend(): Friend
    
    /**
     * 这个[好友][Friend]
     */
    @JvmBlocking(asProperty = true, suffix = "")
    @JvmAsync(asProperty = true, suffix = "")
    override suspend fun user(): Friend
    
    public companion object Key : BaseEventKey<FriendEvent>("api.friend", UserEvent) {
        override fun safeCast(value: Any): FriendEvent? = doSafeCast(value)
    }
}


public suspend inline fun <R> FriendEvent.inFriend(block: Friend.() -> R): R = friend().let(block)
public suspend inline fun <R> FriendEvent.useFriend(block: (Friend) -> R): R = friend().let(block)


// endregion


// region organization event
/**
 * 一个与 [组织][Organization] 相关的事件。
 */
@BaseEvent
public interface OrganizationEvent : ObjectiveEvent {
    /**
     * 这个[组织][Organization]
     */
    @JvmBlocking(asProperty = true, suffix = "")
    @JvmAsync(asProperty = true, suffix = "")
    public suspend fun organization(): Organization
    
    public companion object Key : BaseEventKey<OrganizationEvent>("api.organization") {
        override fun safeCast(value: Any): OrganizationEvent? = doSafeCast(value)
    }
}


public suspend inline fun <R> OrganizationEvent.inOrganization(block: Organization.() -> R): R =
    organization().let(block)

public suspend inline fun <R> OrganizationEvent.useOrganization(block: (Organization) -> R): R =
    organization().let(block)


/**
 * 一个与 [群][Group] 相关的事件。
 */
@BaseEvent
public interface GroupEvent : OrganizationEvent, GroupInfoContainer {
    
    /**
     * 这个[群][Group]
     */
    @JvmBlocking(asProperty = true, suffix = "")
    @JvmAsync(asProperty = true, suffix = "")
    override suspend fun group(): Group
    
    /**
     * 这个[群][Group]
     */
    @JvmBlocking(asProperty = true, suffix = "")
    @JvmAsync(asProperty = true, suffix = "")
    override suspend fun organization(): Group
    
    
    public companion object Key : BaseEventKey<GroupEvent>("api.group", OrganizationEvent) {
        override fun safeCast(value: Any): GroupEvent? = doSafeCast(value)
    }
}


public suspend inline fun <R> GroupEvent.inGroup(block: Group.() -> R): R = group().let(block)
public suspend inline fun <R> GroupEvent.useGroup(block: (Group) -> R): R = group().let(block)


/**
 * 一个与 [频道服务器][Guild] 相关的事件。
 */
@BaseEvent
public interface GuildEvent : OrganizationEvent {
    /**
     * 这个[频道服务器][Guild]
     */
    @JvmBlocking(asProperty = true, suffix = "")
    @JvmAsync(asProperty = true, suffix = "")
    public suspend fun guild(): Guild
    
    /**
     * 这个[频道服务器][Guild]
     */
    @JvmBlocking(asProperty = true, suffix = "")
    @JvmAsync(asProperty = true, suffix = "")
    override suspend fun organization(): Guild
    
    
    public companion object Key : BaseEventKey<GuildEvent>("api.guild", OrganizationEvent) {
        override fun safeCast(value: Any): GuildEvent? = doSafeCast(value)
    }
}


public suspend inline fun <R> GuildEvent.inGuild(block: Guild.() -> R): R = guild().let(block)
public suspend inline fun <R> GuildEvent.useGuild(block: (Guild) -> R): R = guild().let(block)


/**
 * 一个与 [频道][Channel] 相关的事件。
 */
@BaseEvent
public interface ChannelEvent : OrganizationEvent {
    
    /**
     * 这个[频道][Channel]
     */
    @JvmBlocking(asProperty = true, suffix = "")
    @JvmAsync(asProperty = true, suffix = "")
    public suspend fun channel(): Channel
    
    
    /**
     * 这个[频道][Channel]
     */
    @JvmBlocking(asProperty = true, suffix = "")
    @JvmAsync(asProperty = true, suffix = "")
    override suspend fun organization(): Channel
    
    public companion object Key : BaseEventKey<ChannelEvent>("api.channel", OrganizationEvent) {
        override fun safeCast(value: Any): ChannelEvent? = doSafeCast(value)
    }
}


public suspend inline fun <R> ChannelEvent.inChannel(block: Channel.() -> R): R = channel().let(block)
public suspend inline fun <R> ChannelEvent.useChannel(block: (Channel) -> R): R = channel().let(block)


// endregion



