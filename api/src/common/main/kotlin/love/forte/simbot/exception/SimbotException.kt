/*
 *  Copyright (c) 2021-2021 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  根据 Apache License 2.0 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

package love.forte.simbot.exception

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract


/**
 *
 * @author ForteScarlet
 */
public interface SimbotError {
    public val message: String?
    public val cause: Throwable?
}


/**
 *
 * @author ForteScarlet
 */
public open class SimbotException : Exception, SimbotError {
    public constructor() : super()
    public constructor(message: String?) : super(message)
    public constructor(message: String?, cause: Throwable?) : super(message, cause)
    public constructor(cause: Throwable?) : super(cause)
}

/**
 *
 * @author ForteScarlet
 */
public open class SimbotRuntimeException : RuntimeException, SimbotError {
    public constructor() : super()
    public constructor(message: String?) : super(message)
    public constructor(message: String?, cause: Throwable?) : super(message, cause)
    public constructor(cause: Throwable?) : super(cause)
}

/**
 *
 * @author ForteScarlet
 */
public open class SimbotIllegalArgumentException : SimbotError, IllegalArgumentException {
    public constructor() : super()
    public constructor(message: String?) : super(message)
    public constructor(message: String?, cause: Throwable?) : super(message, cause)
    public constructor(cause: Throwable?) : super(cause)
}

/**
 *
 * @author ForteScarlet
 */
public open class SimbotIllegalStateException : SimbotError, IllegalArgumentException {
    public constructor() : super()
    public constructor(message: String?) : super(message)
    public constructor(message: String?, cause: Throwable?) : super(message, cause)
    public constructor(cause: Throwable?) : super(cause)
}



