/*
 *
 *  * Copyright (c) 2021. ForteScarlet All rights reserved.
 *  * Project  simple-robot
 *  * File     MiraiAvatar.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *
 */

package love.forte.simbot.annotation;

import java.lang.annotation.*;

/**
 * 标记一个监听函数为 <b>异步</b> 函数。
 *
 * @author ForteScarlet
 * @see love.forte.simbot.listener.ListenerFunction
 *
 * @since 2.3.0
 */
@Retention(RetentionPolicy.RUNTIME)    //注解会在class字节码文件中存在，在运行时可以通过反射获取到
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE}) //接口、类、枚举、注解、方法
@Documented
public @interface Async {
}
