package com.outerspace.game_of_words.ui_layer

import com.outerspace.game_of_words.data_layer.game.GameResultInterface
import kotlinx.coroutines.CoroutineScope

interface GameUIInterface {
    val scope: CoroutineScope
    fun evaluationResult(result: GameResultInterface)
}
