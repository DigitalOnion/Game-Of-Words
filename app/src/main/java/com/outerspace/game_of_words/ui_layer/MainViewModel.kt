package com.outerspace.game_of_words.ui_layer

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.outerspace.game_of_words.data_layer.game.GameBoard
import com.outerspace.game_of_words.data_layer.game.GameBoardInterface
import com.outerspace.game_of_words.data_layer.game.GameCell
import com.outerspace.game_of_words.data_layer.game.GameResultInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val gameBoard: GameBoard
): ViewModel(), GameBoardInterface, GameUIInterface {

    val liveResult: MutableLiveData<GameResultInterface> by lazy {
        MutableLiveData<GameResultInterface>()
    }

    override val scope = viewModelScope

    override fun evaluationResult(result: GameResultInterface) {
        liveResult.value = result
    }

    override fun getGameBoard(nCols:Int, nRows: Int, gameUi: GameUIInterface): List<List<GameCell>> {
        return gameBoard.getGameBoard(nCols, nRows, this)
    }
}