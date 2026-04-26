import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKmpLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinxSerialization)
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    androidLibrary {
        namespace = "my.scamshield.shared"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        androidResources {
            enable = true
        }

        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation("org.jetbrains.compose.ui:ui-tooling:${libs.versions.composeMultiplatform.get()}")
            implementation("org.jetbrains.compose.ui:ui-tooling-preview:${libs.versions.composeMultiplatform.get()}")
            implementation(libs.androidx.activity.compose)
            implementation(libs.ktor.client.android)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        commonMain.dependencies {
            implementation("org.jetbrains.compose.runtime:runtime:${libs.versions.composeMultiplatform.get()}")
            implementation("org.jetbrains.compose.foundation:foundation:${libs.versions.composeMultiplatform.get()}")
            implementation("org.jetbrains.compose.material3:material3:${libs.versions.composeMultiplatform.get()}")
            implementation("org.jetbrains.compose.ui:ui:${libs.versions.composeMultiplatform.get()}")
            implementation("org.jetbrains.compose.components:components-resources:${libs.versions.composeMultiplatform.get()}")
            implementation("org.jetbrains.compose.components:components-ui-tooling-preview:${libs.versions.composeMultiplatform.get()}")

            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)

            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.logging)

            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.datetime)

            implementation(libs.voyager.navigator)
            implementation(libs.voyager.screenmodel)
            implementation(libs.voyager.koin)
            implementation(libs.voyager.transitions)

            implementation(libs.multiplatform.settings)
            implementation(libs.multiplatform.settings.no.arg)

            implementation(libs.kermit)
            implementation(libs.compose.material.icons.extended)

            implementation("io.github.alexzhirkevich:compottie:2.1.0")
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.turbine)
            implementation(libs.ktor.client.mock)
        }
    }
}
