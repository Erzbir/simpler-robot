/*
 * Copyright (c) 2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

plugins {
    id("simbot.util-module-conventions")
    `simbot-multiplatform-maven-publish`
    id("simbot.dokka-module-configuration")
}

repositories {
    mavenCentral()
}

kotlin {
    explicitApi()
    
    jvm {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
                javaParameters = true
                freeCompilerArgs = freeCompilerArgs + listOf("-Xjvm-default=all")
            }
        }
        withJava()
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }
    
    js(IR) {
        browser()
        nodejs()
    }
    
    val mainPresets = mutableSetOf<KotlinSourceSet>()
    val testPresets = mutableSetOf<KotlinSourceSet>()
    
    // https://ktor.io/docs/client-supported-platforms.html
    val supportedPlatforms = setOf(
        // iOS
        "iosArm32", "iosArm64", "iosX64", "iosSimulatorArm64",
        // watchOS
        "watchosArm32", "watchosArm64", "watchosX86", "watchosX64", "watchosSimulatorArm64",
        // tvOS
        "tvosArm64", "tvosX64", "tvosSimulatorArm64",
        // macOS
        "macosX64", "macosArm64",
        // Linux
        "linuxX64",
        // Windows
        "mingwX64",
    )
    
    targets {
        presets.filterIsInstance<org.jetbrains.kotlin.gradle.plugin.mpp.AbstractKotlinNativeTargetPreset<*>>()
            .filter { preset ->
                preset.name in supportedPlatforms
            }.forEach { presets ->
                val target = fromPreset(presets, presets.name)
                mainPresets.add(target.compilations["main"].kotlinSourceSets.first())
                testPresets.add(target.compilations["test"].kotlinSourceSets.first())
            }
    }
    
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":simbot-util-api-requestor-core"))
                implementation(libs.ktor.client.core)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        
        val nativeMain by creating {
            dependsOn(commonMain)
        }
        val nativeTest by creating {
            dependsOn(commonTest)
        }
        
        configure(mainPresets) { dependsOn(nativeMain) }
        configure(testPresets) { dependsOn(nativeTest) }
        
    }
    
}

// suppress all
tasks.withType<org.jetbrains.dokka.gradle.DokkaTaskPartial>().configureEach {
    dokkaSourceSets.configureEach {
        suppress.set(true)
        perPackageOption {
            suppress.set(true)
        }
    }
}