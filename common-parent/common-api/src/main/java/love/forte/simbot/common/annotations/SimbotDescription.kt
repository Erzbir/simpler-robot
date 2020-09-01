/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simple-robot-S
 *  * File     SimbotDescription.kt
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

package love.forte.simbot.common.annotations

/**
 * 描述注解
 *
 * 用于作为一个针对某些东西的描述标识
 *
 * @author ForteScarlet <ForteScarlet@163.com>
 * @date 2020/9/2
 * @since
 */
@Target(allowedTargets = [AnnotationTarget.CLASS])
@Retention(AnnotationRetention.SOURCE)
@MustBeDocumented
public annotation class SimbotDescription(vararg val value: String = [])

