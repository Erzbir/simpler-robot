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

package love.forte.simbot.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock


/**
 * 一个类似于 [Deferred] 的支持suspend的懒加载器。
 */
public interface LazyValue<T> : suspend () -> T {

    /**
     * 挂起并等待结果值被加载。如果已经加载则会立即返回。
     * 如果加载过程中出现了异常，则每次调用此函数都会得到那个异常。
     */
    public suspend fun await(): T

    /**
     * @see await
     */
    override suspend fun invoke(): T = await()

    /**
     * 立即得到加载的值。如果尚未加载，则会返回包裹了 [NotInitializedException] 异常的 result。
     *
     * @throws NotInitializedException 当结果值尚未初始化时。
     */
    public val value: Result<T>

    /**
     * 判断是否已经加载完成。
     */
    public val isCompleted: Boolean


    public class NotInitializedException internal constructor() : IllegalStateException()
}


public fun <T> lazyValue(initializer: suspend () -> T): LazyValue<T> {
    return LazyValueImpl(initializer)
}

public fun <T> CoroutineScope.lazyValue(
    init: Boolean = false,
    initializer: suspend () -> T
): LazyValue<T> {
    return if (init) {
        LazyValueImpl(async { initializer() }::await)
    } else {
        LazyValueImpl(initializer)
    }
}

public fun <T> completedLazyValue(value: T): LazyValue<T> {
    return CompletedLazyValue(value)
}

private class CompletedLazyValue<T>(value: T) : LazyValue<T> {
    private val _value = value
    override val value get() = Result.success(_value)
    override suspend fun await(): T = _value
    override val isCompleted: Boolean get() = true
}


private class LazyValueImpl<T>(
    initializer: suspend () -> T,
) : LazyValue<T> {
    private var initializer: (suspend () -> T)? = initializer
    private val lock = Mutex(false)

    // NO_INIT or Result<T>
    @Volatile
    private var _value: Any = NO_INIT // Result<T>

    override suspend fun await(): T {
        val v1 = _value
        if (v1 !== NO_INIT) {
            @Suppress("UNCHECKED_CAST")
            (v1 as Result<T>).getOrThrow()
        }

        return lock.withLock {
            val v2 = _value
            if (v2 !== NO_INIT) {
                @Suppress("UNCHECKED_CAST")
                (v2 as Result<T>).getOrThrow()
            } else {
                val typedResult = kotlin.runCatching { initializer!!() }
                _value = typedResult
                initializer = null
                typedResult.getOrThrow()
            }
        }
    }

    override val isCompleted: Boolean
        get() = _value !== NO_INIT

    override val value: Result<T>
        get() {
            val v = _value
            if (v !== NO_INIT) {
                @Suppress("UNCHECKED_CAST")
                return v as Result<T>
            } else {
                return Result.failure(LazyValue.NotInitializedException())
            }
        }

    @Suppress("ClassName")
    private object NO_INIT
}
