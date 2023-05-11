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

import love.forte.simbot.JSTP
import love.forte.simbot.action.ActionType
import love.forte.simbot.definition.*
import love.forte.simbot.message.doSafeCast


/**
 * 一个与组织成员变动相关的事件。
 *
 * @see MemberIncreaseEvent
 * @see MemberDecreaseEvent
 */
@BaseEvent
public interface MemberChangedEvent :
    ChangedEvent, OrganizationEvent, MemberEvent {
    
    /**
     * 这次组织成员变动的**操作者**。
     *
     * 当这次成员变动是因为变动的这位成员 ([before]或[after]) 自己造成的，那么 [operator] 等同于这位成员。
     * 常见于成员的主动退群。
     *
     * 当这次成员变动是由于这位成员之外的其他人所造成的，那么 [operator] 等同于这位操作者。
     * 常见于某位成员被管理员踢出，或经由某位管理员审核后加入当前群聊等，此时这位管理员便是 [operator].
     *
     * 无法保证当前事件能够获取到 [operator] 的信息，当不支持对此信息进行获取的时候，将会得到 `null`。
     *
     */
    @JSTP
    public suspend fun operator(): MemberInfo?
    
    
    public companion object Key :
        BaseEventKey<MemberChangedEvent>(
            "api.member_changed",
            ChangedEvent, OrganizationEvent, MemberEvent
        ) {
        override fun safeCast(value: Any): MemberChangedEvent? = doSafeCast(value)
    }
}


/**
 * 一个 **成员增加** 事件。
 * 其代表一个组织中增加了某个成员。
 *
 * @see IncreaseEvent
 * @see MemberChangedEvent
 */
public interface MemberIncreaseEvent :
    IncreaseEvent,
    MemberChangedEvent {
    
    /**
     * 成员增加事件发生所在的组织。
     */
    @JSTP
    override suspend fun source(): Organization
    
    /**
     * 增加的[成员][Member]。
     */
    @JSTP
    override suspend fun after(): Member
    
    
    /**
     * 行为类型。主动/被动
     */
    public val actionType: ActionType
    
    public companion object Key :
        BaseEventKey<MemberIncreaseEvent>(
            "api.member_increase",
            IncreaseEvent, MemberChangedEvent
        ) {
        override fun safeCast(value: Any): MemberIncreaseEvent? = doSafeCast(value)
    }
}

/**
 * 一个 **成员减少** 事件。
 * 其代表一个组织中减少了某个成员。
 *
 * @see DecreaseEvent
 * @see MemberChangedEvent
 */
public interface MemberDecreaseEvent :
    DecreaseEvent,
    MemberChangedEvent {
    
    
    /**
     * 离开的[成员][Member]
     */
    @JSTP
    override suspend fun before(): Member
    
    /**
     * 行为类型。主动/被动
     */
    public val actionType: ActionType
    
    public companion object Key :
        BaseEventKey<MemberDecreaseEvent>(
            "api.member_decrease",
            DecreaseEvent, MemberChangedEvent
        ) {
        override fun safeCast(value: Any): MemberDecreaseEvent? = doSafeCast(value)
    }
}


/**
 *
 * [MemberIncreaseEvent] 事件类型的子集，代表为一个发生于 [Group] 的成员增加事件。
 *
 * @see MemberIncreaseEvent
 */
public interface GroupMemberIncreaseEvent : MemberIncreaseEvent, GroupEvent {
    
    /**
     * 成员增加事件发生所在的[群][Group]。
     */
    @JvmSynthetic
    override suspend fun group(): Group
    
    
    /**
     * 成员增加事件发生所在的[群][Group]。
     */
    @JSTP
    override suspend fun source(): Group
    
    /**
     * 增加的[群成员][GroupMember]。
     */
    @JSTP
    override suspend fun after(): GroupMember
    
    /**
     * 增加的[群成员][GroupMember]。
     */
    @JSTP
    override suspend fun member(): GroupMember
    
    
    override val key: Event.Key<out GroupMemberIncreaseEvent>
    
    public companion object Key : BaseEventKey<GroupMemberIncreaseEvent>(
        "api.group_member_increase",
        MemberIncreaseEvent, GroupEvent
    ) {
        override fun safeCast(value: Any): GroupMemberIncreaseEvent? = doSafeCast(value)
    }
    
}

/**
 *
 * [MemberIncreaseEvent] 事件类型的子集，代表为一个发生于 [Guild] 的成员增加事件。
 *
 * @see MemberIncreaseEvent
 */
public interface GuildMemberIncreaseEvent : MemberIncreaseEvent, GuildEvent {
    
    /**
     * 成员增加事件发生所在的[频道服务器][Guild]。
     */
    @JvmSynthetic
    override suspend fun guild(): Guild
    
    
    /**
     * 成员增加事件发生所在的[频道服务器][Guild]。
     */
    @JSTP
    override suspend fun source(): Guild
    
    
    /**
     * 增加的[频道成员][GuildMember]。
     */
    @JSTP
    override suspend fun after(): GuildMember
    
    
    /**
     * 增加的[频道成员][GuildMember]。
     */
    @JSTP
    override suspend fun member(): GuildMember
    
    
    override val key: Event.Key<out GuildMemberIncreaseEvent>
    
    public companion object Key : BaseEventKey<GuildMemberIncreaseEvent>(
        "api.guild_member_increase",
        MemberIncreaseEvent, GuildEvent
    ) {
        override fun safeCast(value: Any): GuildMemberIncreaseEvent? = doSafeCast(value)
    }
}

/**
 *
 * [MemberDecreaseEvent] 的子类型事件，代表为一个发生于 [Group] 的成员减少事件。
 *
 * @see MemberDecreaseEvent
 */
public interface GroupMemberDecreaseEvent : MemberDecreaseEvent, GroupEvent {
    
    
    /**
     * 成员减少事件发生所在的[群][Group]。
     */
    @JvmSynthetic
    override suspend fun group(): Group
    
    
    /**
     * 成员减少事件发生所在的[群][Group]。
     */
    @JSTP
    override suspend fun source(): Group
    
    
    /**
     * 减少的[群成员][GroupMember]。
     */
    @JSTP
    override suspend fun before(): GroupMember
    
    
    /**
     * 减少的[群成员][GroupMember]。
     */
    @JSTP
    override suspend fun member(): GroupMember
    
    
    override val key: Event.Key<out GroupMemberDecreaseEvent>
    
    public companion object Key : BaseEventKey<GroupMemberDecreaseEvent>(
        "api.group_member_decrease",
        MemberDecreaseEvent, GroupEvent
    ) {
        override fun safeCast(value: Any): GroupMemberDecreaseEvent? = doSafeCast(value)
    }
    
}

/**
 *
 * [MemberDecreaseEvent] 的子类型事件，代表一个发生在 [Guild] 中的事件。
 *
 * @see MemberDecreaseEvent
 */
public interface GuildMemberDecreaseEvent : MemberDecreaseEvent, GuildEvent {
    
    /**
     * 成员减少事件发生所在的[频道服务器][Guild]。
     */
    @JvmSynthetic
    override suspend fun guild(): Guild
    
    
    /**
     * 成员减少事件发生所在的[频道服务器][Guild]。
     */
    @JSTP
    override suspend fun source(): Guild
    
    
    /**
     * 减少的[频道成员][GuildMember]。
     */
    @JSTP
    override suspend fun before(): GuildMember
    
    
    /**
     * 减少的[频道成员][GuildMember]。
     */
    @JSTP
    override suspend fun member(): GuildMember
    
    
    override val key: Event.Key<out GuildMemberDecreaseEvent>
    
    public companion object Key : BaseEventKey<GuildMemberDecreaseEvent>(
        "api.guild_member_decrease",
        MemberDecreaseEvent, GuildEvent
    ) {
        override fun safeCast(value: Any): GuildMemberDecreaseEvent? = doSafeCast(value)
    }
}







