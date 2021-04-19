/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  component-onebot
 *  * File     AdditionalApi.kt
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
 *
 * 额外的API接口。
 *
 * 此接口定义一个 **额外API**。
 *
 * 不同的组件下，可能默认的送信器API无法完全体现出其完整的功能，因此为三个送信器接口提供一个方法入口，
 * 此方法通过一个 [额外API][AdditionalApi] 来使用一个组件所提供的专属API。
 *
 * 接口的实现由各个组件进行，并应当对其的实现有着详细的描述。
 *
 *
 * 额外的API接口中，[R] 代表这个API所最终的返回值类型，这个类型必须是 [Result] 类型。
 *
 *
 * @since 2.0.6~2.1.0
 */
@SimbotExperimentalApi("尚在测试阶段")
interface AdditionalApi<R : Result> {

    /**
     * 额外API的描述名称。
     * 一般用于出现异常或输出日志的时候所需要的信息。
     */
    val additionalApiName: String



}