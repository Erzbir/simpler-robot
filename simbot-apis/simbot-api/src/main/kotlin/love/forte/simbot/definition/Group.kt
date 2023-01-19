/*
 * Copyright (c) 2021-2023 ForteScarlet <ForteScarlet@163.com>
 *
 * 本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x 、simbot3 等) 的一部分。
 * simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 * 发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 * 你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 * https://www.gnu.org/licenses
 * https://www.gnu.org/licenses/gpl-3.0-standalone.html
 * https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 */

package love.forte.simbot.definition

import love.forte.simbot.ID
import love.forte.simbot.JST
import love.forte.simbot.JSTP
import love.forte.simbot.Timestamp
import love.forte.simbot.utils.item.Items


/**
 * 一个群。
 * @author ForteScarlet
 */
public interface Group : ChatRoom, GroupInfo {
    
    override val bot: GroupBot
    override val id: ID
    override val name: String
    override val icon: String
    override val description: String
    override val createTime: Timestamp
    override val ownerId: ID
    
    @JSTP
    override suspend fun owner(): GroupMember
    
    override val maximumMember: Int
    override val currentMember: Int
    
    /**
     * 获取当前群内的成员序列。
     */
    override val members: Items<GroupMember>
    
    
    // region member
    
    /**
     * 根据ID获取到指定的成员。
     */
    @JST(blockingBaseName = "getMember", blockingSuffix = "", asyncBaseName = "getMember")
    override suspend fun member(id: ID): GroupMember?
    // endregion
    
}


/**
 * 群组信息。
 */
public interface GroupInfo : OrganizationInfo
