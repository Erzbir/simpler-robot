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

package love.forte.simbot.event

/**
 * 此注解标记一个 [Event] 类型并将其标记为一个 **基础事件类型**。
 *
 * ## 基础事件
 * 属于 **基础事件** 的所有事件类型, 它们的主要作用是为其他子事件类型提供语义、类型或属性支撑，
 * 而其本身没有很强的可监听性。
 *
 * 同时一般被标记为 [BaseEvent] 的事件可能会存在多个子事件类型，因此他们涉及的事件范围会很大。
 *
 * 因此被标记为 [BaseEvent] 的事件类型通常**不建议**直接进行监听，而是应该选择它们之下语义更加明确的事件类型。
 *
 *
 * 此注解目前仅做标记使用。
 *
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
@MustBeDocumented
public annotation class BaseEvent
