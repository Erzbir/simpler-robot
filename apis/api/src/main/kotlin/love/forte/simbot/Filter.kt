/*
 *  Copyright (c) 2021 ForteScarlet <https://github.com/ForteScarlet>
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
 * 过滤器, 对一个指定的目标进行过滤匹配, 并得到最终的匹配结果。
 *
 * 此过滤器也同样是一个可挂起的。
 *
 * @author ForteScarlet
 */
public interface Filter<T> {

    /**
     * 通过匹配目标进行检测，得到匹配结果。
     *
     */
    public suspend fun test(t: T): Boolean

}