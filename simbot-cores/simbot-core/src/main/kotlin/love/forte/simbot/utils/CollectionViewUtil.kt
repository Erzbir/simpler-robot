/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x ) 的一部分。
 *
 *  simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 */

@file:JvmName("CollectionViewUtil")

package love.forte.simbot.utils

import org.jetbrains.annotations.UnmodifiableView

/**
 * 将目标列表实例转化为一个 [列表视图][ListView].
 */
public fun <T> List<T>.view(): @UnmodifiableView ListView<T> = ListView(this)



/**
 * 列表视图，对一个目标列表进行代理包装并作为一个 **只读** 视图使用。
 * 此视图不可修改，但是内部元素可能会随着原始的 [代理目标][delegate] 的变化而变化。
 *
 */
public class ListView<T>(private val delegate: List<T>) : List<T> by delegate {
    override fun toString(): String = delegate.toString()
    override fun hashCode(): Int = delegate.hashCode()
    override fun equals(other: Any?): Boolean {
        return if (other is ListView<*>) delegate == other.delegate
        else delegate == other
    }
    
}
