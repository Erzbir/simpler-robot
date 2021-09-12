/*
 *
 *  * Copyright (c) 2021. ForteScarlet All rights reserved.
 *  * Project  simple-robot
 *  * File     MiraiAvatar.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *
 */

@file:Suppress("unused")
@file:JvmName("KaiheilaBots")
package love.forte.simbot.component.kaiheila

import love.forte.simbot.LogAble
import love.forte.simbot.api.message.containers.BotInfo
import love.forte.simbot.component.kaiheila.api.ApiConfiguration
import org.slf4j.Logger
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract


/**
 * 一个开黑啦Bot的信息实例。
 *
 * 参考 [开黑啦 - 机器人](https://developer.kaiheila.cn/bot)
 *
 *
 * 机器人连接模式有两种：webSocket 和 webhook.
 *
 *
 * @author ForteScarlet
 */
public interface KhlBot : LogAble, BotInfo {

    /**
     * bot所属的logger
     */
    override val log: Logger

    /**
     * 与网络日志相关的logger
     */
    val networkLog: Logger

    /**
     * 此bot所使用的api配置信息。
     */
    val apiConfiguration: ApiConfiguration

    /**
     * Client id.
     */
    val clientId: String

    override val botCode: String
        get() = clientId

    override val botCodeNumber: Long
        get() = error("KHL Bot's client does not support numbers.")

    /**
     * token info. 可以重新生成，因此也允许运行时更新。
     */
    var token: String


    /**
     * client secret. 可以重新生成，因此也允许运行时更新。
     */
    var clientSecret: String


    /**
     * 连接模式。
     */
    val connectionMode: ConnectionMode


    /**
     * 启用这个bot
     */
    suspend fun start()


    /**
     * 终止这个bot.
     */
    suspend fun close(cause: Throwable? = null)

}



/**
 * 使用Websocket协议通讯的bot。
 */
public interface WebsocketBot : KhlBot {
    override val connectionMode: ConnectionMode
        get() = ConnectionMode.WEBSOCKET
}

/**
 * 使用Webhook方式的bot。
 */
public interface WebhookBot : KhlBot {
    override val connectionMode: ConnectionMode
        get() = ConnectionMode.WEBHOOK
    // 他还需要一些额外的参数。

    /**
     * Verify Token. 可以重新生成。
     */
    var verifyToken: String


    /**
     * Encrypt Key. 可以重新生成，可以为空。
     */
    var encryptKey: String?
}




public enum class ConnectionMode {
    WEBSOCKET, WEBHOOK
}



public inline fun KhlBot.requireMode(mode: ConnectionMode, msg: () -> String = { "KaiheilaBot's connection mode require $mode, but not." }) {
    if (this.connectionMode != mode) {
        throw IllegalStateException(msg())
    }
}

public inline fun KhlBot.requireWebsocketMode(msg: () -> String = { "KaiheilaBot's connection mode require Websocket, but not." }): WebsocketBot {
    if (this.isWebsocketBot()) return this else throw IllegalStateException(msg())
}

public inline fun KhlBot.requireWebhookMode(msg: () -> String = { "KaiheilaBot's connection mode require Webhook, but not." }): WebhookBot {
    if (this.isWebhookBot()) return this else throw IllegalStateException(msg())
}


public fun KhlBot.checkMode(mode: ConnectionMode): Boolean = connectionMode == mode


@OptIn(ExperimentalContracts::class)
public fun KhlBot.isWebsocketBot(): Boolean {
    contract {
        returns(true) implies (this@isWebsocketBot is WebsocketBot)
    }
    return checkMode(ConnectionMode.WEBSOCKET)
}


@OptIn(ExperimentalContracts::class)
public fun KhlBot.isWebhookBot(): Boolean {
    contract {
        returns(true) implies (this@isWebhookBot is WebhookBot)
    }
    return checkMode(ConnectionMode.WEBSOCKET)
}
