/*
 * Copyright (c) 2022-2023 ForteScarlet <ForteScarlet@163.com>
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

package love.forte.simbot.core.application

import kotlinx.coroutines.Job
import love.forte.simbot.application.Application
import love.forte.simbot.application.BotManagers
import love.forte.simbot.bot.Bot
import love.forte.simbot.bot.BotManager
import love.forte.simbot.bot.BotVerifyInfo
import love.forte.simbot.bot.ComponentMismatchException
import love.forte.simbot.logger.LoggerFactory
import org.slf4j.Logger
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.cancellation.CancellationException


/**
 *
 * @author ForteScarlet
 */
public abstract class BaseApplication : Application {
    abstract override val coroutineContext: CoroutineContext
    protected abstract val logger: Logger

    override val botManagers: BotManagers by lazy { BotManagersImpl(providers.filterIsInstance<BotManager<*>>()) }


    private val job: Job? get() = coroutineContext[Job]

    override suspend fun join() {
        job?.join()
    }

    override suspend fun shutdown(reason: Throwable?) {
        job?.cancel(reason?.let { CancellationException(reason) })
        stopAll(reason)
    }

    protected open suspend fun stopAll(reason: Throwable?) {
        providers.forEach {
            kotlin.runCatching {
                it.cancel(reason)
            }.getOrElse { e ->
                logger.error("Event provider $it cancel failure.", e)
            }
        }
    }
}


private class BotManagersImpl(private val botManagers: List<BotManager<*>>) : BotManagers,
    List<BotManager<*>> by botManagers {

    override fun register(botVerifyInfo: BotVerifyInfo): Bot? {
        logger.info("Registering bot with verify info [{}]", botVerifyInfo)
        for (manager in this) {
            try {
                return manager.register(botVerifyInfo).also { bot ->
                    logger.debug(
                        "Bot verify info [{}] is registered as [{}] via manager [{}]",
                        botVerifyInfo,
                        bot,
                        manager
                    )
                }
            } catch (ignore: ComponentMismatchException) {
                logger.debug("Bot verify info [{}] is not matched by manager {}, try next.", botVerifyInfo, manager)

            }
        }

        return null
    }

    override fun toString(): String {
        return "BotManagersImpl(managers=$botManagers)"
    }

    companion object {
        private val logger = LoggerFactory.getLogger(BotManagersImpl::class)
    }
}
