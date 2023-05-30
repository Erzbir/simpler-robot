/*
 * Copyright (c) 2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.delegate

import love.forte.simbot.Timestamp
import kotlin.reflect.KProperty


/**
 * 将一个**毫秒时间戳**委托为 [Timestamp].
 *
 * e.g.
 * ```kotlin
 * val now by timestamp { System.currentTimeMillis() }
 * ```
 *
 * 特别的，如果你希望委托时间为当前时间，即 [System.currentTimeMillis]，
 * 可以使用 [TimestampDelegate.now] 快速书写：
 *
 * ```kotlin
 * val now by timestamp { now }
 * ```
 *
 * ## 作用域与应用场景
 *
 * [TimestampDelegate.getValue] 委托主要应用于对外的公开属性场景，
 * 通过委托来减少某个对象在初始化时产生过多的简单包装器。
 *
 * 你需要避免一些错误的做法：
 *
 * 1. 避免在一个类的私有作用域使用此委托，例如使用一个私有属性作为委托目标如下：
 *
 * ```kotlin
 * // Bad
 * class Foo {
 *    // 私有属性没有代理的必要
 *    private val now by timestamp { now }
 * }
 * ```
 *
 * 你可以修改为
 *
 * ```kotlin
 * // Good
 * class Foo {
 *     private val nowMillis = System.currentTimeMillis()
 *     // 委托对外公开的属性而非内部应用的属性
 *     private val timestamp by timestamp { nowMillis }
 * }
 * ```
 *
 * 2. 避免在局部作用域使用此委托，例如一个方法中如下：
 *
 * ```kotlin
 * // Bad
 * fun foo() {
 *    val now by timestamp { now }
 *
 *    // 下面会产生3个 Timestamp 对象
 *    useTimestamp(now)
 *    useTimestamp(now)
 *    useTimestamp(now)
 * }
 * ```
 *
 * 你可以修改为
 *
 * ```kotlin
 * fun foo() {
 *    // 直接使用 Timestamp 本身的API即可。
 *    val now = Timestamp.now()
 *    useTimestamp(now)
 *    useTimestamp(now)
 *    useTimestamp(now)
 * }
 * ```
 *
 *
 * @since 3.1.0
 *
 * @see Timestamp
 *
 */
@Suppress("NOTHING_TO_INLINE")
public inline operator fun TimestampDelegate.getValue(o: Any?, property: KProperty<*>?): Timestamp =
    Timestamp.byMillisecond(value)

/**
 * 用于在 [TimestampDelegate.getValue] 中作为委托接收器，通过 [timestamp] 构造。
 *
 * @since 3.1.0
 *
 * @see TimestampDelegate.getValue
 */
@JvmInline
public value class TimestampDelegate @PublishedApi internal constructor(@PublishedApi internal val value: Long) {
    public companion object {
        /**
         * 在 [timestamp] 作用域中可以用于快速委托为 [System.currentTimeMillis] 时间。
         *
         * ```kotlin
         * val now by timestamp { now }
         * ```
         *
         */
        public inline val now: Long get() = System.currentTimeMillis()
    }
}

/**
 * 构造一个 [TimestampDelegate] 对象，用于进行 [Timestamp] 的属性委托。
 *
 * @since 3.1.0
 *
 * @see TimestampDelegate.getValue
 */
public inline fun timestamp(block: TimestampDelegate.Companion.() -> Long): TimestampDelegate =
    TimestampDelegate(block(TimestampDelegate))
