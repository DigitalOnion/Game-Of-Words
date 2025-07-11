package com.outerspace.game_of_words.ui_layer

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.outerspace.game_of_words.data_layer.game.GameBoard
import com.outerspace.game_of_words.data_layer.game.GameResultInterface
import com.outerspace.game_of_words.data_layer.game.GameRules
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(val gameRules: GameRules): ViewModel(), GameUIInterface {
    init {
        gameRules.gameUi = this
    }

    val liveResult: MutableLiveData<GameResultInterface> by lazy {
        MutableLiveData<GameResultInterface>()
    }

    override val scope = viewModelScope

    override fun evaluateResult(result: GameResultInterface) {
        liveResult.value = result
    }

    override fun onClickClearButton() {
        GameBoard.gameRules.clear()
        GameBoard.gameRules.evaluateContent()
    }
}
