
import dev.icerock.gradle.MRVisibility
import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    id("dev.icerock.mobile.multiplatform-resources") version "0.23.0"
    kotlin("plugin.serialization") version "1.9.22"
    id("com.google.gms.google-services")
}
kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "11"
            }
        }
    }
    applyDefaultHierarchyTemplate()
    //jvm("desktop")
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
//            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.ui)
            implementation(compose.material)
            @OptIn(ExperimentalComposeLibrary::class)
            implementation(compose.components.resources)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.uiTooling)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.negotiation)
            implementation(libs.ktor.client.serialization)
            implementation(libs.voyager.navigator)
            implementation(libs.voyager.screenModel)
            implementation(libs.voyager.bottomSheetNavigator)
            implementation(libs.voyager.tabNavigator)
            implementation(libs.voyager.transitions)
            implementation(compose.preview)
            //implementation(libs.compose.ui.tooling.preview)

            implementation(libs.moko.resources)
            implementation(libs.moko.resources.compose)

            implementation(libs.settings)
            implementation(libs.ktor.client.auth)

            implementation(libs.cash.paging.common.compose)
            implementation(libs.cash.paging.common)
            implementation(libs.markdown)
            implementation(libs.accompanist.permissions.v0340)

            implementation(project.dependencies.platform("com.google.firebase:firebase-bom:32.7.2"))
            //noinspection UseTomlInstead
            implementation("com.google.firebase:firebase-analytics")
            implementation(libs.biometry.compose)

            implementation(libs.mvvm.compose) // api mvvm-core, getViewModel for Compose Multiplatform
            implementation(libs.mvvm.flow.compose) // api mvvm-flow, binding extensions for Compose Multiplatform
            implementation(libs.mvvm.livedata.compose) // api mvvm-livedata, binding extensions for Compose Multiplatform
        }
        val androidMain by getting {
            dependsOn(commonMain.get())
        }

        androidMain.dependencies {
            //implementation(libs.compose.ui.tooling.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.ktor.client.cio)
        }
    }
}

android {
    namespace = "com.apu.unsession"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        applicationId = "com.apu.unsession"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
        vectorDrawables {
            useSupportLibrary = true
        }
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    dependencies {
        debugImplementation(libs.compose.ui.tooling)
        implementation(libs.androidx.core.splashscreen)
    }
}
dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.ui.tooling.preview.android)
    implementation(libs.firebase.messaging.ktx)
}
multiplatformResources {
    multiplatformResourcesPackage = "com.apu.unsession" // required
    multiplatformResourcesClassName = "MR" // optional, default MR
    multiplatformResourcesVisibility = MRVisibility.Public // optional, default Public
    iosBaseLocalizationRegion = "en" // optional, default "en"
    multiplatformResourcesSourceSet = "commonMain" // optional, default "commonMain"
}
compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Exe, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.apu.unsession"
            packageVersion = "1.0.0"
        }
    }
}

compose.experimental {
    web.application {
    }
}
