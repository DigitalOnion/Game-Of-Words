package com.outerspace.game_of_words.ui_layer

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.outerspace.game_of_words.data_layer.data.DictionaryApiService
import com.outerspace.game_of_words.data_layer.game.CellSpec
import com.outerspace.game_of_words.data_layer.game.GameResultInterface
import com.outerspace.game_of_words.data_layer.game.GameRules
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel
@Inject constructor(val gameRules: GameRules,
                    private val dictionaryService: DictionaryApiService):
    ViewModel(), UiLayerInterface {

    override val liveResult: MutableLiveData<GameResultInterface> by lazy {
        MutableLiveData<GameResultInterface>()
    }

    override val scope = viewModelScope

    override fun onClickClearButton() {
        gameRules.clear()
        gameRules.evaluateContent(content = "", scope = viewModelScope, uiLayer = this, dictionaryService = dictionaryService)
    }

    override fun onClickKeyboardButton(cell: CellSpec, cellList: MutableList<CellSpec>) {
        gameRules.swapCells(cell, cellList)
        gameRules.append(cell.face)
        val content = gameRules.content()
        gameRules.evaluateContent(content, viewModelScope, this, dictionaryService)
    }
}
