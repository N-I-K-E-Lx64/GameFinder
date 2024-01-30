package de.hive.gamefinder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import de.hive.gamefinder.di.KoinInit
import de.hive.gamefinder.ui.theme.Theme
import org.koin.android.ext.koin.androidContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        KoinInit().init {
            androidContext(androidContext = this@MainActivity)
        }

        setContent {
            Theme {
                App()
            }
        }
    }
}


@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
