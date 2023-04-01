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

package love.forte.simbot.definition

import kotlinx.coroutines.flow.firstOrNull
import love.forte.simbot.ID
import love.forte.simbot.JSTP
import love.forte.simbot.Timestamp
import love.forte.simbot.action.MuteSupport
import love.forte.simbot.bot.Bot
import love.forte.simbot.utils.item.Items
import kotlin.time.Duration

/**
 * 一个组织下的成员。
 *
 * [Member] 作为 _联系人_ 的一种，实现 [Contact],
 * 但是 [Member] 可能无法被 [ContactsContainer] 直接检索。
 *
 * @see GuildMember
 * @see GroupMember
 */
// 考虑实现 Contact
public interface Member : Contact, MemberInfo, MuteSupport {
    
    override val id: ID
    override val bot: Bot
    
    /**
     * 这个成员所属的组织。一般来讲，一个 [Member] 实例不会同时存在于 [Group] 和 [Channel].
     */
    @JSTP
    public suspend fun organization(): Organization
    
    /**
     * 在客观条件允许的情况下，对其进行禁言。
     * 此行为不会捕获异常。
     *
     * @param duration 禁言时长. 大于0时有效。
     *
     * @see MuteSupport.mute
     */
    @JvmSynthetic
    override suspend fun mute(duration: Duration): Boolean
    
    /**
     * 当前群成员在其所属组织内所扮演/拥有的角色。
     */
    public val roles: Items<Role>
    
    
    /**
     * 判断当前成员是否拥有"管理者"这样的角色。
     *
     * @see Role.isAdmin
     */
    @JSTP
    public suspend fun isAdmin(): Boolean = roles.asFlow().firstOrNull { it.isAdmin } != null
    
    /**
     * 判断当前成员是否拥有"拥有者"这样的角色。
     *
     * @see Organization.ownerId
     */
    @JSTP
    public suspend fun isOwner(): Boolean = organization().ownerId == id
    
}


/**
 * 一个频道服务器下的成员。
 */
public interface GuildMember : Member {
    /**
     * 这个成员所属的频道服务器。
     */
    @JSTP
    public suspend fun guild(): Guild
    
    
    /**
     * 这个成员所属的频道服务器。
     */
    @JSTP
    override suspend fun organization(): Guild = guild()
}


public interface GroupMember : Member {
    /**
     * 这个成员所属的群。
     */
    @JSTP
    public suspend fun group(): Group
    
    
    /**
     * 这个成员所属的群。
     */
    @JSTP
    override suspend fun organization(): Group = group()
}


/**
 * 一个成员信息。
 *
 * [MemberInfo] 支持解构：
 * ```kotlin
 * val (id, username, avatar, nickname) = memberInfo
 * ```
 *
 * > 前三个属性的解构扩展来自于 [UserInfo]。
 *
 */
public interface MemberInfo : UserInfo {
    override val id: ID
    override val username: String
    override val avatar: String
    
    /**
     * 此成员在当前组织下的昵称。
     * [nickname]不可为null，当一个群成员不存在群昵称的时候，[nickname] 为空字符串。
     *
     * @see nickOrUsername
     */
    public val nickname: String
    
    /**
     * 此成员加入当前组织的时间。
     *
     * _不被支持的可能性很大。_
     *
     */
    public val joinTime: Timestamp
    
    /**
     * 优先尝试获取 [nickname], 如果 [nickname] 为空，则使用 [username].
     */
    public val nickOrUsername: String get() = nickname.ifEmpty { username }
    
}


/**
 * 尝试通过 [MemberInfo.nickOrUsername] 获取当前用户的有效名称(不为空的)。
 * 如果最终结果依旧为空，得到null。
 */
public inline val MemberInfo.validName: String? get() = nickOrUsername.ifEmpty { null }

/**
 * 尝试获取 [MemberInfo] 中不为全空白字符串的有效名称。
 * 如果最终的结果依旧仅为空白字符串，得到null。
 */
public inline val MemberInfo.notBlankValidName: String? get() = nickname.ifBlank { username.ifBlank { null } }


/**
 * [MemberInfo] 属性解构扩展，第4个属性，相当于 [MemberInfo.nickname]。
 * ```kotlin
 * val (id, username, avatar, nickname) = memberInfo
 * ```
 *
 * > 前三个属性的解构扩展来自于 [UserInfo]。
 */
@Suppress("NOTHING_TO_INLINE")
public inline operator fun MemberInfo.component4(): String = nickname