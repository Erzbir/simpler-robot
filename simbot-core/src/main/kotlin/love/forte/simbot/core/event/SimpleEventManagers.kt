/*
 * Copyright (c) 2021-2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.core.event


/**
 * 构建一个 [SimpleEventListenerManager].
 *
 * 可以选择提供一个初始的 [SimpleListenerManagerConfiguration]。
 */
public inline fun simpleListenerManager(
    initial: SimpleListenerManagerConfiguration = SimpleListenerManagerConfiguration(),
    block: SimpleListenerManagerConfiguration.() -> Unit,
): SimpleEventListenerManager {
    return SimpleEventListenerManager.newInstance(initial.also(block))
}