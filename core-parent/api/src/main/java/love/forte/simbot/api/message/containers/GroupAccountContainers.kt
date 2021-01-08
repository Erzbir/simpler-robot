/*
 *
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  component-onebot
 * File     AccountContainerBranches.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 *
 *
 *
 */
@file:JvmName("Containers")
@file:JvmMultifileClass

package love.forte.simbot.api.message.containers

import love.forte.simbot.annotation.ContainerType


/**
 * 群用户名称信息容器
 */
@ContainerType("群用户名称信息容器")
public interface GroupAccountNameContainer : AccountNameContainer

/**
 * 群用户头衔信息容器
 */
@ContainerType("群用户头衔信息容器")
public interface GroupAccountAvatarContainer : AccountAvatarContainer

/**
 * 群用户账号信息容器
 */
@ContainerType("群用户账号信息容器")
public interface GroupAccountCodeContainer : AccountCodeContainer

/**
 * 群用户头衔信息容器。
 */
@ContainerType("群用户头衔信息容器")
public interface GroupAccountTitleContainer : Container {
    /**
     * 获取此用户的群头衔。
     * 在不支持或者无头衔的情况下获取为`null`。
     */
    val accountTitle: String?
}

/**
 * 群成员禁言信息容器。
 */
@ContainerType("群成员禁言信息容器")
public interface GroupAccountMuteContainer : Container {
    /**
     * 禁言信息容器，在不支持的情况下得到 `-1`。
     */
    val muteTime: Long
}


/**
 * 一个群用户账号信息容器, 继承了
 * - [群用户名称容器][GroupAccountNameContainer],
 * - [群用户头像容器][GroupAccountAvatarContainer],
 * - [群用户账号容器][GroupAccountCodeContainer],
 * - [群用户头衔容器][GroupAccountTitleContainer],
 */
@ContainerType("群账户信息容器")
public interface GroupAccountInfo : Container, AccountInfo,
    GroupAccountNameContainer, GroupAccountAvatarContainer,
    GroupAccountCodeContainer, GroupAccountTitleContainer,
    GroupAccountMuteContainer, AnonymousContainer {

    /**
     * 判断当前用户是否为 ** 匿名用户 **，当不支持匿名的时候则会恒返回 `false`。
     */
    override val anonymous: Boolean
        get() = false

}


/**
 * 群成员账号信息。
 */
@ContainerType("群成员账号信息")
public interface GroupAccountContainer : Container, AccountContainer {
    /**
     * 获取一个群成员账号信息。
     */
    override val accountInfo: GroupAccountInfo
}


/**
 * 获取一个 [GroupAccountContainer] 数据实例。
 */
@JvmName("getGroupAccountContainer")
@Suppress("FunctionName")
public fun GroupAccountContainer(accountInfo: GroupAccountInfo): GroupAccountContainer =
    GroupAccountContainerData(accountInfo)

/**
 * 获取一个 [GroupAccountContainer] 数据实例。
 * java直接使用上面那个。
 */
@JvmName("__getGroupAccountContainer")
@Suppress("FunctionName")
public inline fun GroupAccountContainer(accountInfo: () -> GroupAccountInfo): GroupAccountContainer =
    GroupAccountContainer(accountInfo())


/** [GroupAccountContainer] 数据类实现。 */
private data class GroupAccountContainerData(override val accountInfo: GroupAccountInfo) : GroupAccountContainer



