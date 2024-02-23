
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
    // alias(libs.plugins.multiplatform.resources)

    // id("dev.hydraulic.conveyor") version "1.8"
}

// version = "0.1.0"

java {
    toolchain {
        vendor = JvmVendorSpec.JETBRAINS
        languageVersion = JavaLanguageVersion.of(17)
    }
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }
    
    jvm("desktop") {
        jvmToolchain {
            vendor = JvmVendorSpec.JETBRAINS
            languageVersion = JavaLanguageVersion.of(17)
        }
    }
    
    sourceSets {
        val desktopMain by getting
        
        androidMain.dependencies {
            implementation(libs.compose.ui.tooling.preview)

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

            //api(libs.moko.resources)
            //api(libs.moko.resourcesCompose)

            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            @OptIn(ExperimentalComposeLibrary::class)
            implementation(compose.components.resources)
            implementation(compose.materialIconsExtended)

            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.auth)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.client.contentNegotiation)
            implementation(libs.ktor.serialization.json)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.coroutines.core)

            implementation(libs.napier)

            implementation(libs.voyager.navigator)
            implementation(libs.voyager.koin)

            implementation(libs.sqlDelight.runtime)
            implementation(libs.sqlDelight.coroutines)
            implementation(libs.sqlDelight.primitives)

            implementation(libs.kamel)
            implementation(libs.compose.shimmer)

            implementation(libs.material3.window.size.multiplatform)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)

            implementation(libs.logback)

            implementation(libs.compose.ui.tooling.preview)

            implementation(libs.ktor.client.desktop)
            implementation(libs.kotlinx.coroutines.swing)

            implementation(libs.sqlDelight.jvm)

            //implementation(libs.jewel.int.ui.standalone)
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
        debugImplementation(libs.compose.ui.tooling.preview)
    }
}

compose.desktop {
    application {
        mainClass = "de.hive.gamefinder.MainKt"

        val iconsRoot = project.file("src/desktopMain/resources")

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "de.hive.gamefinder"
            packageVersion = "0.1.0"
            macOS {
                packageVersion = "1.0.0"
            }
            windows {
                iconFile.set(iconsRoot.resolve("icons/appIcon.ico"))
            }
            linux {
                iconFile.set(iconsRoot.resolve("icons/appIcon.png"))
            }

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

    defaultConfigs {
        buildConfigField(FieldSpec.Type.STRING, "CLIENT_ID", clientId)
        buildConfigField(FieldSpec.Type.STRING, "CLIENT_SECRET", clientSecret)
    }
}

sqldelight {
    databases {
        create("GameFinderDatabase") {
            packageName.set("de.hive.gamefinder.database")
        }
    }
}

tasks {
    withType<JavaExec> {
        // afterEvaluate is needed because the Compose Gradle Plugin
        // register the task in the afterEvaluate block
        afterEvaluate {
            javaLauncher = project.javaToolchains.launcherFor {
                languageVersion = JavaLanguageVersion.of(17)
                vendor = JvmVendorSpec.JETBRAINS
            }
            setExecutable(javaLauncher.map { it.executablePath.asFile.absolutePath }.get())
        }
    }
}

/*dependencies {
    linuxAmd64(compose.desktop.linux_x64)
    macAarch64(compose.desktop.macos_arm64)
    windowsAmd64(compose.desktop.windows_x64)
}*/


/*multiplatformResources {
    multiplatformResourcesPackage = "de.hive.gamefinder"
}*/

/*// region Work around temporary Compose bugs.
configurations.all {
    attributes {
        // https://github.com/JetBrains/compose-jb/issues/1404#issuecomment-1146894731
        attribute(Attribute.of("ui", String::class.java), "awt")
    }
}
// endregion*/
