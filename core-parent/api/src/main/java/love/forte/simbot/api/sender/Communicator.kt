/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  component-onebot
 *  * File     Communicator.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *  *
 *  *
 *
 */

package love.forte.simbot.api.sender

import love.forte.simbot.api.SimbotExperimentalApi
import love.forte.simbot.api.message.results.Result


/**
 * 通讯器接口，为三个送信器的统一父接口。
 *
 *
 * 通讯器接口定义一个 [additional]
 *
 *
 *
 * @see Setter
 * @see Getter
 * @see Sender
 */
public interface Communicator {

    /**
     * 执行一个额外的API。
     *
     * 对于返回值的null可能性，由 [AdditionalApi] 的实现进行决定。
     */
    @SimbotExperimentalApi
    fun <R : Result> additionalExecute(additionalApi: AdditionalApi<R>): R

}