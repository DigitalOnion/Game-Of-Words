package com.outerspace.game_of_words.data_layer.game

import com.outerspace.game_of_words.ui_layer.GameUIInterface
import kotlinx.coroutines.CoroutineScope

class GameCell(val face: String,
               val rules: GameRules,
               ) {

    fun onClick() {
        rules.appendKey(face)
        val content = rules.content()
        rules.evaluateContent()
    }
}