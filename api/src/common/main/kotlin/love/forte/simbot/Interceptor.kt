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

package love.forte.simbot

/**
 *
 * 拦截器.
 *
 * @author ForteScarlet
 */
public sealed interface Interceptor<C: Interceptor.Content> {

    /**
     * 对当前指定的拦截内容进行处理。
     *
     * 想要继续流程则使用 [Content.proceed] 进入到下一个拦截器，或者进入正常流程。
     */
    public suspend fun intercept(context: C)


    // 一个
    public interface Content {
        public suspend fun proceed()
    }
}



