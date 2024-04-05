package de.hive.gamefinder.feature.navigation

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import de.hive.gamefinder.core.application.port.`in`.FriendUseCase
import de.hive.gamefinder.core.application.port.`in`.GameUseCase
import de.hive.gamefinder.core.domain.Friend
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NavigationScreenModel(private val friendUseCase: FriendUseCase, private val gameUseCase: GameUseCase) : ScreenModel {

    private val _friendName = MutableStateFlow<String>("")
    val friendName = _friendName.asStateFlow()
    fun setFriendName(name: String) {
        _friendName.value = name
    }

    private val _shortlistBadge = MutableStateFlow<Int>(0)
    val shortlistBadge = _shortlistBadge.asStateFlow()

    fun validateFriend(friendName: String): Boolean {
        return friendUseCase.checkFriendExistence(friendName = friendName)
    }

    fun saveFriend() {
        screenModelScope.launch {
            val friend = Friend(name = friendName.value)
            friendUseCase.addFriend(friend)
        }
    }

    fun getBadgeValues() {
        screenModelScope.launch {
            gameUseCase.getGamesOnShortlist().collect {
                _shortlistBadge.value = it.size
            }
        }
    }
}