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

package love.forte.simbot.logger

import love.forte.simbot.logger.internal.AbstractSimpleLogger
import love.forte.simbot.logger.internal.toDisplayName


/**
 *
 * @author ForteScarlet
 */
internal class SimpleConsoleLogger(
    private val name: String,
    override val displayName: String = name.toDisplayName(),
    override val level: LogLevel = LogLevel.INFO
): AbstractSimpleLogger() {
    override fun getName(): String = name
    
    private fun printLog(shortLevel: String, log: String, throwable: Throwable?) {
        println("[$displayName] $shortLevel: $log")
        throwable?.printStackTrace()
    }
    
    override fun trace0(formattedLog: String, throwable: Throwable?) {
        printLog("t", formattedLog, throwable)
    }
    
    override fun debug0(formattedLog: String, throwable: Throwable?) {
        printLog("d", formattedLog, throwable)
    }
    
    override fun info0(formattedLog: String, throwable: Throwable?) {
        printLog("i", formattedLog, throwable)
    }
    
    override fun warn0(formattedLog: String, throwable: Throwable?) {
        printLog("w", formattedLog, throwable)
    }
    
    override fun error0(formattedLog: String, throwable: Throwable?) {
        printLog("e", formattedLog, throwable)
    }
}
