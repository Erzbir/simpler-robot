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

package love.forte.simbot.action

import love.forte.simbot.SimbotRuntimeException


/**
 * 一个 **行为动作**。
 *
 * 不论是对于 [love.forte.simbot.Bot], [组织][love.forte.simbot.definition.Organization] 还是 [用户][love.forte.simbot.definition.User] 等，
 *
 * 尽管他们在不同组件中的能力与含义有所不同，但是它们总会存在一部分相似的能力与行为, 例如对于一个群成员，
 * 在权限足够的情况下，有很多平台都允许对其进行禁言或踢出。
 *
 * [Action] 对这些常见的行为进行描述，假若组件存在支持的相似功能，优先考虑实现相关接口。
 *
 * [Action] 所规定的全部行为动作都应是异步的。
 *
 * @author ForteScarlet
 */
public interface Action


/**
 * 一个行为回执。
 * 行为回执出现在一些与行为相关的地方，并描述本次行为所得到的一次结果。
 * 行为回执最基本的能力是描述本次行为 [是否成功][isSuccess]。
 *
 * 存在 [isSuccess] 但是这不意味着 [ActionReceipt] 会对任何异常进行处理。对于 [isSuccess] 的可能性，
 * 通常情况下仅代表合法情况下的执行结果，不代表异常情况下的执行结果。
 *
 * 假如对应行为过程中出现了非法操作等非正常流程行为，应当直接抛出对应异常。
 *
 *
 * 对于此接口的其他实现或继承，如果仅仅描述 [isSuccess], 考虑提供默认实现减少对象实例。
 *
 */
public interface ActionReceipt {

    /**
     * 正常流程下此次行为是否成功。
     */
    public val isSuccess: Boolean
}


/**
 * 一个行为异常。
 */
public open class ActionException : SimbotRuntimeException {
    public constructor() : super()
    public constructor(message: String?) : super(message)
    public constructor(message: String?, cause: Throwable?) : super(message, cause)
    public constructor(cause: Throwable?) : super(cause)
}