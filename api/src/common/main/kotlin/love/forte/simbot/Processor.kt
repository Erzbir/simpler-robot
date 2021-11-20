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
 * 处理器，针对于一个目标进行处理的函数。
 *
 * @author ForteScarlet
 */
public interface Processor<T, R> {

    /**
     * 对目标进行处理。
      */
    public suspend fun process(target: T): R

}


/**
 * 一个 [异常][Throwable] [处理器][Processor].
 */
public interface ExceptionProcessor<T : Throwable, R> : Processor<T, R>