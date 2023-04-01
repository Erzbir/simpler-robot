/*
 * Copyright (c) 2022-2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simboot.spring.autoconfigure

import kotlinx.serialization.ExperimentalSerializationApi
import love.forte.simboot.core.application.BootApplicationConfiguration
import love.forte.simbot.ExperimentalSimbotApi
import love.forte.simbot.application.Application
import love.forte.simbot.application.EventProvider
import love.forte.simbot.bot.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.context.annotation.Bean
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import java.io.FileNotFoundException


/**
 * 用于配置bot的扫描与自动注册的配置类。
 * @author ForteScarlet
 */
@AutoConfigureAfter(SimbotSpringBootListenerAutoRegisterBuildConfigure::class)
public open class SimbotSpringBootBotAutoRegisterBuildConfigure {

    @Bean
    @Order(Ordered.LOWEST_PRECEDENCE - 1)
    public fun simbotSpringBootBotAutoRegisterPostProcessor(
        @Autowired(required = false) customDecoderFactories: List<BotVerifyInfoDecoderFactory<*, *>>? = null,
    ): ApplicationPostProcessor {
        return SimbotSpringBootBotAutoRegisterPostProcessor(customDecoderFactories ?: emptyList())
    }

    private class SimbotSpringBootBotAutoRegisterPostProcessor(
        private val customDecoderFactories: List<BotVerifyInfoDecoderFactory<*, *>>,

        ) : ApplicationPostProcessor {
        override suspend fun processApplication(application: Application) {
            logger.debug("Start bot auto register.")
            config(application, customDecoderFactories)
        }


        @OptIn(ExperimentalSerializationApi::class, ExperimentalSimbotApi::class)
        private suspend fun config(
            application: Application,
            customDecoderFactories: List<BotVerifyInfoDecoderFactory<*, *>>
        ) {
            val resolver = PathMatchingResourcePatternResolver()

            val decoderList = customDecoderFactories + StandardBotVerifyInfoDecoderFactory.supportDecoderFactories()

            val configuration = application.configuration as? BootApplicationConfiguration

            val botConfigResources = (configuration?.botConfigurationResources ?: emptyList())
                .asSequence()
                .flatMap {
                    try {
                        resolver.getResources(it).asList()
                    } catch (fne: FileNotFoundException) {
                        logger.warn(
                            "Can not resolve resource path 「{}」: {}, just use empty resources.",
                            it,
                            fne.localizedMessage
                        )
                        emptyList()
                    }
                }
                .filter {
                    if (it.filename == null) {
                        logger.warn("Resource [{}]'s filename is null, skip.", it)
                    }

                    it.filename != null
                }
                .distinct()
                .mapNotNull {
                    logger.debug("Resolved bot register resource: {}", it)
                    val decoderFactory = decoderList.findLast { decoder -> decoder.match(it.filename!!) }
                    // ?: null // err? warn?

                    if (decoderFactory == null) {
                        // 没有任何解码器能匹配此资源。
                        logger.warn("No decoders match bot resource [{}] in {}.", it, decoderList)
                        return@mapNotNull null
                    }

                    var botVerifyInfo: BotVerifyInfo? = null

                    if (it.isFile) {
                        kotlin.runCatching {
                            botVerifyInfo =
                                it.file.takeIf { f -> f.exists() }?.toPath()?.toBotVerifyInfo(decoderFactory)
                                    ?: return@runCatching
                        }.getOrNull()
                    }

                    botVerifyInfo ?: it.url.toBotVerifyInfo(decoderFactory)
                }.toList()

            if (botConfigResources.isNotEmpty()) {
                botConfigResources.forEach { res ->
                    register(application.providers, res).also { bot ->
                        if (bot != null) {
                            val isAutoStart =
                                (application.configuration as? BootApplicationConfiguration)?.isAutoStartBots == true
                            if (isAutoStart) {
                                val startResult = bot.start()
                                logger.info(
                                    "Bot info [{}] successfully registered as Bot(component={}, id={}) and the result of auto-start is {}",
                                    res,
                                    bot.component,
                                    bot.id,
                                    startResult
                                )
                            } else {
                                // Why?
                                val because: String = when (application.configuration) {
                                    is BootApplicationConfiguration -> "the configuration property 'autoStartBots' is false"
                                    else -> "the type of application configuration is not BootApplicationConfiguration"
                                }
                                
                                logger.info(
                                    "Bot info [{}] successfully registered as Bot(component={}, id={}]) but it does not start automatically because {}",
                                    res,
                                    bot.component,
                                    bot.id,
                                    because
                                )
                            }
                        } else {
                            logger.warn(
                                "Bot verify info [{}] not registered by any registrars, skip. The botRegistrar: {}",
                                res,
                                this
                            )
                        }
                    }
                }
            }

        }

        private fun register(providers: List<EventProvider>, botVerifyInfo: BotVerifyInfo): Bot? {
            logger.info("Registering bot with verify info [{}]", botVerifyInfo)
            for (manager in providers) {
                if (manager !is BotRegistrar) {
                    continue
                }

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
                    // ignore this.
                }
            }

            logger.warn("Bot verify info [{}] is not matched by any manager, skip it.", botVerifyInfo)
            return null
        }
    }


    public companion object {
        private val logger = LoggerFactory.getLogger(SimbotSpringBootBotAutoRegisterBuildConfigure::class.java)
    }
}