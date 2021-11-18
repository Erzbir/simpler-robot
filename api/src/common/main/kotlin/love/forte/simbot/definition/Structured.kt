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

package love.forte.simbot.definition

/**
 * 一个 **结构化** 的定义。
 * 结构化的东西，他可以有一个 [上级][previous]，以及一个 [下级][next]。
 *
 * @author ForteScarlet
 */
public interface Structured<P, N> {

    /**
     * 上一级的内容。
     */
    public val previous: P


    /**
     * 下一级的内容。
     */
    public val next: N
}