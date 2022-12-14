/*
 * Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
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

package love.forli.test

import love.forte.simboot.annotation.AnnotationEventFilterFactory
import love.forte.simboot.annotation.Filter
import love.forte.simboot.annotation.Filters
import love.forte.simboot.annotation.Listener
import love.forte.simboot.spring.autoconfigure.EnableSimbot
import love.forte.simbot.ExperimentalSimbotApi
import love.forte.simbot.MutableAttributeMap
import love.forte.simbot.event.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Component


@EnableSimbot
@SpringBootApplication
open class SpringBootApp


@OptIn(ExperimentalSimbotApi::class)
fun main(vararg args: String) {
    runApplication<SpringBootApp>(args = args).also { context ->
        //println(context.getBean("myFilterFactory"))
    }
}

@Component
open class MyListener {

    @Filter(by = MyFilterFactory::class)
    @Listener
    fun FriendMessageEvent.listen(){}

}

@Component
open class MyInterceptor : EventProcessingInterceptor {
    override suspend fun intercept(context: EventProcessingInterceptor.Context): EventProcessingResult {
        return context.proceed()
    }
}

@Component
open class MyFilterFactory @Autowired constructor(private val filter: MyEventFilter) : AnnotationEventFilterFactory {
    override fun resolveFilter(
        listener: EventListener,
        listenerAttributes: MutableAttributeMap,
        filter: Filter,
        filters: Filters
    ): EventFilter {
        return this.filter
    }
}

@Component
open class MyEventFilter : EventFilter {
    override suspend fun test(context: EventListenerProcessingContext): Boolean {
        println("FILTER!")
        return true
    }
}
