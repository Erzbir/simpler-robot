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

val kotlinVersion = "1.6.0"

plugins {
    // kotlin("gradle-plugin") version "1.5.31"
    // kotlin version "1.6.0"
    kotlin("jvm") version "1.6.0" apply false
    kotlin("multiplatform") version "1.6.0" apply false
    // kotlin("jvm") version "1.6.0" apply false
    id("org.jetbrains.dokka") version "1.5.30" apply false
    kotlin("plugin.serialization") version "1.6.0" apply false
}

println()

extra.properties.forEach { (t, u) ->
    println("ext.$t\t=\t$u")
}

println()

group = "love.forte.simple-robot"
version = "3.0.0-preview"

repositories {
    mavenCentral()
}

subprojects {
    // apply(plugin = "kotlin")
}







