
import com.codingfeline.buildkonfig.compiler.FieldSpec
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree
import org.jetbrains.kotlin.konan.properties.Properties

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose)
    alias(libs.plugins.android.application)

    alias(libs.plugins.kotlinx.serialization)

    alias(libs.plugins.sqlDelight.plugin)
    alias(libs.plugins.buildKonfig)

    id("dev.hydraulic.conveyor") version "1.9"
}

version = "1.1.0"


kotlin {
    jvmToolchain {
        languageVersion = JavaLanguageVersion.of(17)
        vendor = JvmVendorSpec.JETBRAINS
    }

    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        instrumentedTestVariant.sourceSetTree.set(KotlinSourceSetTree.test)
    }

    jvm("desktop")
    
    sourceSets {
        val desktopMain by getting

        androidMain.dependencies {
            implementation(compose.uiTooling)
            implementation(libs.androidx.activity.compose)

            implementation(libs.ktor.client.android)
            implementation(libs.kotlinx.coroutines.android)
            implementation(libs.sqlDelight.android)
        }
        commonMain.dependencies {
            api(libs.koin.core)
            api(libs.koin.compose)

            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(compose.materialIconsExtended)

            implementation(libs.kermit)

            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.auth)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.client.contentNegotiation)
            implementation(libs.ktor.serialization.json)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.coroutines.core)

            implementation(libs.voyager.navigator)
            implementation(libs.voyager.koin)

            implementation(libs.settings.noArg)
            implementation(libs.settings.coroutines)

            implementation(libs.sqlDelight.runtime)
            implementation(libs.sqlDelight.coroutines)
            implementation(libs.sqlDelight.primitives)

            implementation(libs.material3.window.size.multiplatform)

            implementation(libs.composeIcons.featherIcons)

            implementation(libs.coil)
            implementation(libs.coil.network.ktor)
            implementation(libs.compose.shimmer)
            implementation(libs.reorderable)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs) { exclude(group = "org.jetbrains.compose.material") }

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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

compose.desktop {
    application {
        mainClass = "de.hive.gamefinder.MainKt"

        nativeDistributions {
            packageName = "gamefinder"
            copyright = "© 2024 Niklas Schünemann. All rights reserved."

            modules("java.sql")

            buildTypes.release.proguard {
                //obfuscate.set(true)
                configurationFiles.from(project.file("compose-desktop.pro"))
            }
        }
    }
}

buildkonfig {
    packageName = "de.hive.gamefinder"

    val props = Properties()
    if (rootProject.file("secrets.properties").exists()) {
        val secretProperties = rootProject.file("secrets.properties")

        try {
            props.load(secretProperties.inputStream())
        } catch (ex: Exception) {
            println(ex)
        }
    }

    val clientId = if (props.getProperty("CLIENT_ID") != null) props.getProperty("CLIENT_ID") else System.getenv("CLIENT_ID")
    val clientSecret = if (props.getProperty("CLIENT_SECRET") != null) props.getProperty("CLIENT_SECRET") else System.getenv("CLIENT_SECRET")
    val development = if (props.getProperty("DEVELOPMENT") != null) "true" else "false"

    defaultConfigs {
        buildConfigField(FieldSpec.Type.STRING, "CLIENT_ID", clientId)
        buildConfigField(FieldSpec.Type.STRING, "CLIENT_SECRET", clientSecret)
        buildConfigField(FieldSpec.Type.BOOLEAN, "DEVELOPMENT", development)
    }
}

sqldelight {
    databases {
        create("GameFinderDatabase") {
            packageName.set("de.hive.gamefinder.database")
        }
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

tasks {
    withType<JavaExec> {
        // afterEvaluate is needed because the Compose Gradle Plugin register the task in the afterEvaluate block
        afterEvaluate {
            javaLauncher = project.javaToolchains.launcherFor { languageVersion = JavaLanguageVersion.of(17) }
            setExecutable(javaLauncher.map { it.executablePath.asFile.absoluteFile }.get())
        }
    }
}

/*configurations.all {
    attributes {
        attribute(Attribute.of("ui", String::class.java), "awt")
    }
}*/