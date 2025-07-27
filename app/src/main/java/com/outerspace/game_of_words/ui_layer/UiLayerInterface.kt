package com.outerspace.game_of_words.ui_layer

import androidx.lifecycle.MutableLiveData
import com.outerspace.game_of_words.data_layer.game.CellSpec
import com.outerspace.game_of_words.data_layer.game.GameResult
import com.outerspace.game_of_words.data_layer.game.GameResultInterface
import kotlinx.coroutines.CoroutineScope

interface UiLayerInterface {
    val scope: CoroutineScope
    val liveResult: MutableLiveData<GameResultInterface>
    fun onClickClearButton()
    fun onClickKeyboardButton(cell: CellSpec, cellList: MutableList<CellSpec>)
}
