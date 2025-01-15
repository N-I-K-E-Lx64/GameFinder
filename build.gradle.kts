plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader#
    alias(libs.plugins.multiplatform).apply(false)
    alias(libs.plugins.compose.compiler).apply(false)
    alias(libs.plugins.compose).apply(false)
    alias(libs.plugins.android.application).apply(false)
    alias(libs.plugins.kotlinx.serialization).apply(false)
    alias(libs.plugins.buildKonfig).apply(false)
}

repositories {
    maven("https://packages.jetbrains.team/maven/p/kpm/public/")
}