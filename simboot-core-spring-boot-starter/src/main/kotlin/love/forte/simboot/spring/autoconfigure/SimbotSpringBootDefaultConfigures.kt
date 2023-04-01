/*
 * Copyright (c) 2022-2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simboot.spring.autoconfigure

import org.springframework.context.annotation.Import

/**
 * The default configures.
 *
 * @author ForteScarlet
 */
@Import(
    // 监听函数bean注册
    EventListenerRegistryPostProcessor::class,
    // default binders
    DefaultBinderFactoryConfigure::class,
    // listeners
    SimbotListenerMethodProcessor::class,
    SimbotSpringBootComponentAutoInstallBuildConfigure::class,
    SimbotSpringBootEventProviderAutoInstallBuildConfigure::class,
    SimbotSpringBootInterceptorsAutoConfigure::class,
)
public open class SimbotSpringBootDefaultConfigures