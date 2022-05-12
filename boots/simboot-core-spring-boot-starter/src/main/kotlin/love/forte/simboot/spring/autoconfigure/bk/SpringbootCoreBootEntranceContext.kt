// /*
//  *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
//  *
//  *  本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x ) 的一部分。
//  *
//  *  simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
//  *
//  *  发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
//  *
//  *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
//  *  https://www.gnu.org/licenses
//  *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
//  *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
//  *
//  */
//
// package love.forte.simboot.spring.autoconfigure.bk
//
// import love.forte.simboot.factory.BeanContainerFactory
// import love.forte.simboot.factory.ConfigurationFactory
// import love.forte.simbot.BotVerifyInfoDecoder
// import love.forte.simbot.DecoderBotVerifyInfo
// import love.forte.simbot.event.EventListenerManager
// import org.springframework.core.io.Resource
// import java.io.InputStream
//
// /**
//  *
//  * @author ForteScarlet
//  */
// public class SpringbootCoreBootEntranceContext(
//     private val _configurationFactory: ConfigurationFactory,
//     private val _beanContainerFactory: BeanContainerFactory,
//     private val _listenerManager: EventListenerManager,
// ) {
//     //
//     // override fun getConfigurationFactory(): ConfigurationFactory {
//     //     return _configurationFactory
//     // }
//     //
//     // override fun getBeanContainerFactory(): BeanContainerFactory {
//     //     return _beanContainerFactory
//     // }
//     //
//     // @OptIn(ExperimentalSimbotApi::class, ExperimentalSerializationApi::class)
//     // override fun getAllBotInfos(
//     //     configuration: Configuration,
//     //     beanContainer: BeanContainer
//     // ): List<BotVerifyInfo> {
//     //     //
//     //     val resolver = PathMatchingResourcePatternResolver()
//     //     // find file first
//     //     // may java.io.FileNotFoundException
//     //     val resources = try {
//     //         // TODO
//     //         resolver.getResources("file:$BOTS_PATTERN")
//     //     } catch (fnf: FileNotFoundException) {
//     //         logger.warn("Can not resolve resource path 「{}」: {}, just use empty resources.", "file:$BOTS_PATTERN", fnf.localizedMessage)
//     //         emptyArray<Resource>()
//     //     }.ifEmpty {
//     //         try {
//     //             resolver.getResources("classpath:$BOTS_PATTERN")
//     //         } catch (fnf: FileNotFoundException) {
//     //             logger.warn("Can not resolve resource path 「{}」: {}, just use empty resources.", "classpath:$BOTS_PATTERN", fnf.localizedMessage)
//     //             emptyArray<Resource>()
//     //         }
//     //     }
//     //
//     //     val allDecoders = StandardBotVerifyInfoDecoderFactory.supportDecoderFactories(logger)
//     //
//     //     return resources.mapNotNull { resource ->
//     //         val name = resource.filename ?: return@mapNotNull null
//     //         val decoder = allDecoders.find { d -> d.match(name) } ?: return@mapNotNull null
//     //         SpringResourceBotVerifyInfo(decoder.create(), resource)
//     //     }
//     // }
//     //
//     // override fun getListenerManager(beanContainer: BeanContainer): EventListenerManager {
//     //     return _listenerManager
//     // }
//     //
//     // override val logger: Logger = LoggerFactory.getLogger(SpringbootCoreBootEntranceContext::class)
//     //
//     //
//     // public companion object {
//     //     private const val BOTS_PATTERN = "simbot-bots/**.bot"
//     // }
// }
//
//
// private class SpringResourceBotVerifyInfo(
//     override val decoder: BotVerifyInfoDecoder,
//     private val resource: Resource,
// ) : DecoderBotVerifyInfo() {
//     override val name: String get() = resource.filename ?: resource.url.toString()
//     override fun inputStream(): InputStream = resource.inputStream
//
//     override fun close() {
//     }
// }