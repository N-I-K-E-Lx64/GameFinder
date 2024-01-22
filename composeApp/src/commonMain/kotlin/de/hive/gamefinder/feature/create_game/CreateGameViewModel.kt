package de.hive.gamefinder.feature.create_game

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import de.hive.gamefinder.core.application.port.`in`.GameUseCase
import de.hive.gamefinder.core.domain.Game
import de.hive.gamefinder.core.domain.Platform
import kotlinx.coroutines.launch

class CreateGameViewModel(private val useCase: GameUseCase) : ScreenModel {

    private val _gameName = mutableStateOf("")
    val gameName: State<String> = _gameName
    fun setGameName(name: String) {
        _gameName.value = name
    }

    val platforms = Platform.entries.toTypedArray()

    private val _platform = mutableStateOf(Platform.STEAM)
    val platform: State<Platform> = _platform
    fun setPlatform(platform: Platform) {
        _platform.value = platform
    }

    fun addGame(game: Game) {
        screenModelScope.launch {
            useCase.createGame(game)
            setGameName("")
            setPlatform(Platform.STEAM)
        }
    }
}