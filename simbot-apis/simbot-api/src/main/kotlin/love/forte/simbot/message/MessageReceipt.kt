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

package love.forte.simbot.message

import love.forte.simbot.ID
import love.forte.simbot.action.DeleteSupport
import love.forte.simbot.definition.IDContainer


/**
 * 消息回执，当消息发出去后所得到的回执信息。
 * @author ForteScarlet
 */
public interface MessageReceipt : IDContainer, DeleteSupport {

    /**
     * 一个消息回执中存在一个ID.
     */
    override val id: ID

    /**
     * 消息是否发送成功。此属性的 `false` 一般代表在排除其他所有的 **异常情况** 下，在正常流程中依然发送失败（例如发送的消息是空的）。
     * 不代表发送中出现了异常，仅代表在过程完全正常的情况下的发送结果。
     *
     * 假若 [isSuccess] 为 `false`, 那么 [id] 可能会是一个空值。
     */
    public val isSuccess: Boolean
    
    /**
     * 删除此回执所代表的消息。这通常代表为'撤回'相关消息。
     * 如果此回执不支持撤回则可能会恒定的得到 `false`。
     *
     * 进行删除的过程中通常不会捕获任何异常，因此可能抛出任何可能涉及的
     * 业务或状态异常。
     *
     * @return 是否删除成功
     */
    override suspend fun delete(): Boolean
}
