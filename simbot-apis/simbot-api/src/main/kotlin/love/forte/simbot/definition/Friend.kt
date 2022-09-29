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

package love.forte.simbot.definition

import love.forte.simbot.ID
import love.forte.simbot.bot.Bot


/**
 * 一个 **好友**。
 */
public interface Friend : Contact, BotContainer, FriendInfo {
    override val id: ID
    override val bot: Bot
    override val remark: String?
    override val username: String
    override val avatar: String
}

/**
 * 一个好友基本信息。
 *
 * 支持解构：
 * ```kotlin
 * val (id, username, avatar, remark) = friendInfo
 * ```
 *
 * 解构结果前三个属性来自 [UserInfo]。
 *
 */
public interface FriendInfo : UserInfo {
    override val id: ID
    override val username: String
    override val avatar: String
    
    /**
     * 在Bot眼中，一个好友可能存在一个备注。
     */
    public val remark: String?
    
    /**
     * 对于Bot，好友可能存在于一个指定的分组中。
     *
     * @see category
     */
    @Suppress("DEPRECATION_ERROR")
    @Deprecated(
        "No longer used and will be removed. Please refer to 'love.forte.simbot.definition.Category'",
        ReplaceWith("category", "love.forte.simbot.definition.Category"), level = DeprecationLevel.ERROR
    )
    public val grouping: love.forte.simbot.Grouping get() = love.forte.simbot.Grouping.EMPTY
    
    /**
     * 优先尝试获取好友的 [remark], 如果 [remark] 为null，则取其 [username].
     */
    public val remarkOrUsername: String get() = remark ?: username
    
}

/**
 * [FriendInfo] 的解构扩展第4个属性，相当于 [FriendInfo.remark]。
 *
 * 前三个属性来自于 [UserInfo]。
 *
 * ```kotlin
 * val (id, username, avatar, remark) = friendInfo
 * ```
 *
 */
@Suppress("NOTHING_TO_INLINE")
public inline operator fun FriendInfo.component4(): String? = remark

