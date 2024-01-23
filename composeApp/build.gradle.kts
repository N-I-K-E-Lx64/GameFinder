import com.codingfeline.buildkonfig.compiler.FieldSpec
import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.konan.properties.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)

    alias(libs.plugins.serialization)

    alias(libs.plugins.sqlDelight.plugin)
    alias(libs.plugins.buildKonfig)
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }
    
    jvm("desktop")
    
    sourceSets {
        val desktopMain by getting
        
        androidMain.dependencies {
            //implementation(libs.compose.ui.tooling.preview)
            implementation(libs.compose.ui.tooling.preview.desktop)

            implementation(libs.androidx.activity.compose)

            implementation(libs.koin.android)
            implementation(libs.ktor.client.android)
            implementation(libs.kotlinx.coroutines.android)
            implementation(libs.sqlDelight.android)
        }
        commonMain.dependencies {
            api(libs.koin.core)
            api(libs.koin.compose)

            api(libs.multiplatform.noArg)
            api(libs.multiplatform.coroutines)

            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            @OptIn(ExperimentalComposeLibrary::class)
            implementation(compose.components.resources)

            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.auth)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.client.contentNegotiation)
            implementation(libs.ktor.serialization.json)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.coroutines.core)

            implementation(libs.logback)
            implementation(libs.napier)

            implementation(libs.voyager.navigator)
            implementation(libs.voyager.koin)

            implementation(libs.sqlDelight.coroutines)

            implementation(libs.kamel)

            implementation(libs.material3.window.size.multiplatform)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)

            implementation(libs.ktor.client.desktop)
            implementation(libs.kotlinx.coroutines.swing)

            implementation(libs.sqlDelight.jvm)

            implementation(libs.jewel.int.ui.standalone)
            implementation(libs.jewel.int.ui.decoratedWindow)
        }
    }
}

android {
    namespace = "de.hive.gamefinder"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        applicationId = "de.hive.gamefinder"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    dependencies {
        debugImplementation(libs.compose.ui.tooling.preview.desktop)
    }
}

compose.desktop {
    application {
        mainClass = "de.hive.gamefinder.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "de.hive.gamefinder"
            packageVersion = "1.0.0"
        }
    }
}

buildkonfig {
    packageName = "de.hive.gamefinder"

    val props = Properties()
    try {
        props.load(file("secrets.properties").inputStream())
    } catch (e: Exception) {
        println(e)
    }

    defaultConfigs {
        buildConfigField(FieldSpec.Type.STRING, "CLIENT_ID", props.getProperty("client_id"))
        buildConfigField(FieldSpec.Type.STRING, "CLIENT_SECRET", props.getProperty("client_secret"))
    }
}

sqldelight {
    databases {
        create("GameFinderDatabase") {
            packageName.set("de.hive.gamefinder.database")
        }
    }
}