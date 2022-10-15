import love.forte.gradle.common.kotlin.multiplatform.defaultConfig

plugins {
    `simbot-simple-project-setup`
    `simbot-multiplatform-maven-publish`
    id("simbot.dokka-module-configuration")
}

kotlin {
    defaultConfig {
        sourceSetsConfig = {
            commonTest {
                dependencies {
                    // implementation(kotlin("test"))
                    implementation(kotlin("test-annotations-common"))
                    implementation(kotlin("test-common"))
                }
            }
            jvmMain {
                dependencies {
                    api(libs.slf4j.api)
                }
            }
            jvmTest {
                dependencies {
                    implementation(kotlin("test-junit5"))
                }
            }
            jsTest {
                dependencies {
                    implementation(kotlin("test-js"))
                }
            }
        }
    }
}
