-ignorewarnings

-dontwarn ch.qos.logback.**
-dontwarn org.apache.batik.**
-dontwarn com.sun.org.apache.**
-dontwarn com.sun.xml.internal.**
-dontwarn org.apache.commons.logging.**
-dontwarn androidx.compose.material.**
-dontwarn androidx.compose.material3.**

-dontwarn javax.imageio.metadata.**
-dontwarn javax.xml.catalog.**

-keep class com.jetbrains.JBR* { *; }
-dontnote com.jetbrains.JBR*
#-keep class sun.misc.Unsafe { *; }
#-dontnote sun.misc.Unsafe
-keep class com.sun.jna** { *; }
-dontnote com.sun.jna**

# Keep Sqlite driver classes
-keep class org.sqlite.** { *; }

-keep class io.ktor.client.engine.cio.** { *; }

-keep class io.ktor.serialization.kotlinx.json.** { *; }

-keep class kotlinx.coroutines.swing.** { *; }

-keep class ch.qos.logback.classic.spi.** { *; }