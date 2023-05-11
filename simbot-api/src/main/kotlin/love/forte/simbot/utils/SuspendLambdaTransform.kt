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

@file:JvmName("Lambdas")

package love.forte.simbot.utils

import love.forte.simbot.Api4J
import love.forte.simbot.ExperimentalSimbotApi
import java.util.function.*
import java.util.function.Function


// region consumers


/**
 * Kotlin api:
 * ```kotlin
 * fun foo(block: suspend (T) -> Unit) {  }
 * fun bar(block: suspend T.() -> Unit) {  }
 * ```
 *
 * Use it in Java:
 * ```java
 * foo(Lambdas.suspendConsumer(t -> {}));
 * bar(Lambdas.suspendConsumer(t -> {}));
 * ```
 *
 *
 */
@JvmOverloads
@Api4J
@ExperimentalSimbotApi
public fun <T> suspendConsumer(isRunWithInterruptible: Boolean = true, function: Consumer<T>): suspend (T) -> Unit =
    if (isRunWithInterruptible) {
        { runWithInterruptible { function.accept(it) } }
    } else {
        { function.accept(it) }
    }


@JvmOverloads
@Api4J
@ExperimentalSimbotApi
public fun <T1, T2> suspendConsumer(
    isRunWithInterruptible: Boolean = true,
    function: BiConsumer<T1, T2>,
): suspend (T1, T2) -> Unit = if (isRunWithInterruptible) {
    { a, b -> runWithInterruptible { function.accept(a, b) } }
} else {
    { a, b -> function.accept(a, b) }
}

@JvmOverloads
@Api4J
@ExperimentalSimbotApi
public fun <T1, T2, T3> suspendConsumer(
    isRunWithInterruptible: Boolean = true,
    function: Consumer3<T1, T2, T3>,
): suspend (T1, T2, T3) -> Unit = if (isRunWithInterruptible) {
    { a, b, c -> runWithInterruptible { function.accept(a, b, c) } }
} else {
    { a, b, c -> function.accept(a, b, c) }
}

@JvmOverloads
@Api4J
@ExperimentalSimbotApi
public fun <T1, T2, T3, T4> suspendConsumer(
    isRunWithInterruptible: Boolean = true,
    function: Consumer4<T1, T2, T3, T4>,
): suspend (T1, T2, T3, T4) -> Unit = if (isRunWithInterruptible) {
    { a, b, c, d -> runWithInterruptible { function.accept(a, b, c, d) } }
} else {
    { a, b, c, d -> function.accept(a, b, c, d) }
}

@JvmOverloads
@Api4J
@ExperimentalSimbotApi
public fun <T1, T2, T3, T4, T5> suspendConsumer(
    isRunWithInterruptible: Boolean = true,
    function: Consumer5<T1, T2, T3, T4, T5>,
): suspend (T1, T2, T3, T4, T5) -> Unit = if (isRunWithInterruptible) {
    { a, b, c, d, e -> runWithInterruptible { function.accept(a, b, c, d, e) } }
} else {
    { a, b, c, d, e -> function.accept(a, b, c, d, e) }
}

/**
 * Kotlin api:
 * ```kotlin
 * fun foo(block: (T) -> Unit) {  }
 * fun bar(block: T.() -> Unit) {  }
 * ```
 *
 * Use it in Java:
 * ```java
 * foo(Lambdas.eliminateUnit(t -> {}));
 * bar(Lambdas.eliminateUnit(t -> {}));
 * ```
 *
 *
 */
@Api4J
@ExperimentalSimbotApi
public fun <T> eliminateUnit(function: Consumer<T>): (T) -> Unit = function::accept


@Api4J
@ExperimentalSimbotApi
public fun <T1, T2> eliminateUnit(
    function: BiConsumer<T1, T2>,
): (T1, T2) -> Unit = function::accept

@Api4J
@ExperimentalSimbotApi
public fun <T1, T2, T3> eliminateUnit(
    function: Consumer3<T1, T2, T3>,
): (T1, T2, T3) -> Unit = function::accept

@Api4J
@ExperimentalSimbotApi
public fun <T1, T2, T3, T4> eliminateUnit(
    function: Consumer4<T1, T2, T3, T4>,
): (T1, T2, T3, T4) -> Unit = function::accept

@Api4J
@ExperimentalSimbotApi
public fun <T1, T2, T3, T4, T5> eliminateUnit(
    function: Consumer5<T1, T2, T3, T4, T5>,
): (T1, T2, T3, T4, T5) -> Unit = function::accept

@Api4J
@ExperimentalSimbotApi
public fun interface Consumer3<T1, T2, T3> {
    public fun accept(t1: T1, t2: T2, t3: T3)
}

@Api4J
@ExperimentalSimbotApi
public fun interface Consumer4<T1, T2, T3, T4> {
    public fun accept(t1: T1, t2: T2, t3: T3, t4: T4)
}

@Api4J
@ExperimentalSimbotApi
public fun interface Consumer5<T1, T2, T3, T4, T5> {
    public fun accept(t1: T1, t2: T2, t3: T3, t4: T4, t5: T5)
}
// endregion

// region providers & functions

/**
 * Kotlin api:
 * ```kotlin
 * fun foo(block: suspend () -> T) {  }
 * ```
 *
 * Use it in Java:
 * ```java
 * foo(Lambdas.suspendSupplier(() -> new T()));
 * foo(Lambdas.suspendSupplier(T::new));
 * ```
 *
 *
 */
@JvmOverloads
@Api4J
@ExperimentalSimbotApi
public fun <R> suspendSupplier(isRunWithInterruptible: Boolean = true, function: Supplier<R>): suspend () -> R =
    if (isRunWithInterruptible) {
        { runWithInterruptible { function.get() } }
    } else {
        { function.get() }
    }

/**
 * Kotlin api:
 * ```kotlin
 * fun foo(block: suspend (T) -> R) {  }
 * fun bar(block: suspend T.() -> R) {  }
 * ```
 *
 * Use it in Java:
 * ```java
 * foo(Lambdas.suspendFunction(t -> new R()));
 * bar(Lambdas.suspendFunction(t -> new R()));
 * ```
 *
 *
 */
@JvmOverloads
@Api4J
@ExperimentalSimbotApi
public fun <T, R> suspendFunction(isRunWithInterruptible: Boolean = true, function: Function<T, R>): suspend (T) -> R =
    if (isRunWithInterruptible) {
        { runWithInterruptible { function.apply(it) } }
    } else {
        { function.apply(it) }
    }


@JvmOverloads
@Api4J
@ExperimentalSimbotApi
public fun <T1, T2, R> suspendFunction(
    isRunWithInterruptible: Boolean = true,
    function: BiFunction<T1, T2, R>,
): suspend (T1, T2) -> R =
    if (isRunWithInterruptible) {
        { a, b -> runWithInterruptible { function.apply(a, b) } }
    } else {
        { a, b -> function.apply(a, b) }
    }


@JvmOverloads
@Api4J
@ExperimentalSimbotApi
public fun <T1, T2, T3, R> suspendFunction(
    isRunWithInterruptible: Boolean = true,
    function: (T1, T2, T3) -> R,
): suspend (T1, T2, T3) -> R =
    if (isRunWithInterruptible) {
        { a, b, c -> runWithInterruptible { function(a, b, c) } }
    } else {
        { a, b, c -> function(a, b, c) }
    }

@JvmOverloads
@Api4J
@ExperimentalSimbotApi
public fun <T1, T2, T3, T4, R> suspendFunction(
    isRunWithInterruptible: Boolean = true,
    function: (T1, T2, T3, T4) -> R,
): suspend (T1, T2, T3, T4) -> R =
    if (isRunWithInterruptible) {
        { a, b, c, d -> runWithInterruptible { function(a, b, c, d) } }
    } else {
        { a, b, c, d -> function(a, b, c, d) }
    }


@JvmOverloads
@Api4J
@ExperimentalSimbotApi
public fun <T1, T2, T3, T4, T5, R> suspendFunction(
    isRunWithInterruptible: Boolean = true,
    function: (T1, T2, T3, T4, T5) -> R,
): suspend (T1, T2, T3, T4, T5) -> R =
    if (isRunWithInterruptible) {
        { a, b, c, d, e -> runWithInterruptible { function(a, b, c, d, e) } }
    } else {
        { a, b, c, d, e -> function(a, b, c, d, e) }
    }


// endregion






