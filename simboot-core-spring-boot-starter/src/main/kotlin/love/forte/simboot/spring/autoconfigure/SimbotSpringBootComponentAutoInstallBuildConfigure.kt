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

package love.forte.simboot.spring.autoconfigure

import love.forte.simbot.Component
import love.forte.simbot.ComponentFactory
import love.forte.simbot.installAllComponents
import love.forte.simbot.logger.logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean


/**
 * 自动配置当前可加载的所有 [Component] 的配置类。
 *
 * 如果当前spring环境中存在任何额外的[ComponentFactory], 则使用这些 factory;
 * 否则会直接使用 [installAllComponents] 来尝试加载环境中的所有可加载项。
 *
 * For example custom factories:
 *
 * **Kotlin**
 * ```kotlin
 * @Configuration(proxyBeanMethods = false)
 * open class MyCustomFactoryConfiguration {
 *
 *    @Bean
 *    fun fooFactory(): FooComponent.Factory {
 *       return FooComponent
 *    }
 *
 *    @Bean
 *    fun barFactory(): BarFactory {
 *       return BarFactory()
 *    }
 *
 *    // ...
 *
 * }
 * ```
 *
 * **Java**
 * ```java
 * @Configuration(proxyBeanMethods = false)
 * public class MyCustomFactoryConfiguration {
 *
 *    @Bean
 *    public FooComponent.Factory fooFactory() {
 *       return FooComponent.Factory;
 *    }
 *
 *    @Bean
 *    public BarFactory barFactory() {
 *       return new BarFactory();
 *    }
 * }
 * ```
 *
 *
 * @author ForteScarlet
 */
public open class SimbotSpringBootComponentAutoInstallBuildConfigure {

    @Bean
    public fun simbotSpringBootComponentAutoInstallBuildConfigure(
        @Autowired(required = false) factories: List<ComponentFactory<*, *>>? = null
    ): SimbotSpringBootApplicationBuildConfigure {
        val factories0 = factories ?: emptyList()
        return SimbotSpringBootApplicationBuildConfigure { configuration ->
            logger.info("The number of Installable Event Provider Factories is {}", factories0.size)
            if (factories0.isEmpty()) {
                val classLoader = configuration.classLoader
                logger.info("Install components by [installAllComponents] via classLoader {}", classLoader)
                installAllComponents(classLoader)
            } else {
                logger.debug("Install components by: {}", factories)
                factories0.forEach {
                    install(it)
                }
            }
        }
    }


    public companion object {
        private val logger =
            love.forte.simbot.logger.LoggerFactory.logger<SimbotSpringBootComponentAutoInstallBuildConfigure>()
    }
}
