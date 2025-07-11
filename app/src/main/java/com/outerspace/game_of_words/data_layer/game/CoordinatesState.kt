package com.outerspace.game_of_words.data_layer.game

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue

@Stable
class CoordinatesState (
    x: Int,
    y: Int,
) {
    var x by mutableIntStateOf(x)
    var y by mutableIntStateOf(y)

    fun moveTo(moveToX: Int, moveToY:Int) {
        x = moveToX
        y = moveToY
    }
}

