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

package love.forte.simbot.message

import love.forte.simbot.ID
import love.forte.simbot.action.DeleteSupport

/**
 * 一个消息内容，其中存在一个[消息链][Messages]。
 *
 * 对于一个事件，使用 [接收的消息内容][ReceivedMessageContent].
 *
 * @see RemoteMessageContent
 *
 */
public abstract class MessageContent {

    /**
     * 消息内容ID。一般可以通过 `metadata` 获取。
     */
    public abstract val messageId: ID

    /**
     * 接收到的消息链
     */
    public abstract val messages: Messages

    /**
     * 得到这串消息中的所有 [纯文本][PlainText] 消息的拼接字符串。
     */
    public open val plainText: String by lazy(LazyThreadSafetyMode.PUBLICATION) {
        messages.filterIsInstance<PlainText<*>>().joinToString(separator = "", transform = PlainText<*>::text)
    }
}

/**
 * 一个远端消息主体，一般代表通过事件或者查询而得的事件主体。
 *
 * [RemoteMessageContent] 实现 [DeleteSupport], 代表 [RemoteMessageContent] **可能允许** 被删除。
 *
 * @see DeleteSupport
 */
public sealed class RemoteMessageContent : MessageContent(), DeleteSupport {

    /**
     * 此消息的唯一标识。
     */
    abstract override val messageId: ID
    
    /**
     * 尝试删除当前消息。
     *
     * 如果此消息的实现不支持这种删除行为，则可能直接返回固定值 `false`；
     * 如果此消息由于诸如**权限**等原因而导致无法删除，则可能会导致对应的异常。
     *
     */
    @JvmSynthetic
    abstract override suspend fun delete(): Boolean
 
}


/**
 * 一个接收到的 [MessageContent], 即事件中的 [MessageContent], 代表一个被动获取的消息。
 */
public abstract class ReceivedMessageContent : RemoteMessageContent()


/**
 * 一个获得的 [MessageContent], 即主动获取的消息，常见于历史消息获取。
 */
public abstract class ObtainedMessageContent : RemoteMessageContent()


/**
 * 一个本地的消息实体，一般代表本地构建的非远端存在的消息。
 *
 * 本地构建的消息也许不存在 metadata
 *
 */
public abstract class LocalMessageContent : MessageContent()



