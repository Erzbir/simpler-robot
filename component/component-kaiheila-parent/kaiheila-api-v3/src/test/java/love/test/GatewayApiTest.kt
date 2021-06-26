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

package love.test

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import love.forte.simbot.component.kaiheila.api.v3.*
import org.slf4j.LoggerFactory
import kotlin.test.Test


/**
 *
 * @author ForteScarlet
 */
class GatewayApiTest {

    companion object {
        const val clientId = GatewayApiConstant.clientId
        const val token = GatewayApiConstant.token
        const val clientSecret = GatewayApiConstant.clientSecret
    }

    val logger = LoggerFactory.getLogger(GatewayApiTest::class.java)

    @Test
    fun logTest() {
        logger.debug("Debug!")
    }

    @Test
    fun apiTest() {
        val gatewayReq = GatewayReq(1)

        logger.debug("gatewayReq: {}", gatewayReq)

        val b = V3WsBot(
            clientId = clientId,
            token = token,
            clientSecret = clientSecret,
            client = client,
            configuration = v3BotConfiguration {
                apiConfiguration {
                    api = V3
                }
            }
        )

        val scope = CoroutineScope(Dispatchers.Default)
        scope.launch {
            b.start()
        }

        while (true) {}

    }


}