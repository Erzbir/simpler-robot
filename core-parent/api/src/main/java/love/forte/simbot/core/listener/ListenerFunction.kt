/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     ListenerFunction.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.core.listener

import love.forte.simbot.core.api.message.MsgGet
import love.forte.simbot.core.constant.PriorityConstant
import love.forte.simbot.core.filter.AtDetection
import love.forte.simbot.core.filter.ListenerFilter
import java.lang.reflect.Type


/**
 * [ListenerFunction] 的 [排序规则][Comparator] 实例。
 */
public val ListenerFunctionComparable: Comparator<ListenerFunction> = Comparator { f1, f2 -> f1.priority.compareTo(f2.priority) }



/**
 * 监听函数。
 *
 * @see love.forte.simbot.core.annotation.Listen
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
public interface ListenerFunction {

    /**
     * 此监听函数的唯一编号，用于防止出现重复冲突。
     * 在判断重复性前，会优先判断此id，再使用equals。
     * 如果id相同，则认为其相同，则不再使用equals判断。
     */
    val id: String

    /**
     * 监听函数的名称。
     */
    val name: String


    /**
     * 监听函数的优先级，默认为 [普通第十级][PriorityConstant.TENTH].
     */
    @JvmDefault
    val priority: Int get() = PriorityConstant.TENTH


    /**
     * 获取此监听函数上可以得到的注解。
     */
    fun <A : Annotation> getAnnotation(type: Class<out A>) : A?


    /**
     * 监听的类型们。
     */
    val listenTypes: List<Class<out MsgGet>>


    /**
     * 此监听函数对应的监听器列表。
     */
    val filters: List<ListenerFilter>


    /**
     * 判断当前监听函数是否可以触发当前类型的监听.
     */
    @JvmDefault
    fun <T: MsgGet> canListen(onType: Class<T>): Boolean {
        return listenTypes.any { it.isAssignableFrom(onType) }
    }


    /**
     * 当前监听函数的载体。例如一个 Class。
     * 一般如果是个method，那么此即为[Class]。
     */
    val type: Type


    /**
     * 执行监听函数并返回一个执行后的响应结果。
     *
     * @throws Throwable 执行可能会存在任何可能发生的以外异常。而 [ListenResult] 中包含的一般仅仅是方法执行时候出现的异常。
     */
    operator fun invoke(data: ListenerFunctionInvokeData): ListenResult<*>
}


/**
 * 监听函数执行所需要的数据。
 */
public interface ListenerFunctionInvokeData {
    /** 监听到的消息。 */
    val msgGet: MsgGet
    /** 监听函数上下文。 */
    val context: ListenerContext
    /** at检测器。 */
    val atDetection: AtDetection
}


/**
 * 监听器已经存在异常。
 */
public class ListenerAlreadyExistsException : IllegalStateException {
    constructor() : super()
    constructor(s: String?) : super(s)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)
}


/**
 * 监听器不存在异常。
 */
public class ListenerNotExistsException : IllegalStateException {
    constructor() : super()
    constructor(s: String?) : super(s)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)
}



/**
 * 判断监听函数是否相等。
 */
internal fun ListenerFunction.funcEquals(other: Any?): Boolean {
    if(this === other) return true

    if(other !is ListenerFunction) return false

    if(this.id == other.id) return true
    return this == other
}

/**
 * 用于distinct的实例。
 */
public class ListenerFunctionDistinction(private val func: ListenerFunction) {
    override operator fun equals(other: Any?): Boolean = func.funcEquals(other)
    override fun hashCode(): Int {
        return func.hashCode()
    }
}

