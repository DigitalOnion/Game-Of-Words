package com.outerspace.game_of_words.data_layer.game

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

class GameCell ( val face: String,
                val rules: GameRulesInterface,
                ) {

    lateinit var x: MutableState<Dp>
    lateinit var y: MutableState<Dp>

    @Composable
    fun rememberCoordinates (xBtn: Int, yBtn: Int): GameCell {
        x = remember { mutableStateOf(xBtn.dp) }
        y = remember { mutableStateOf(yBtn.dp) }
        return this
    }

    fun swapCellPosition(otherCell: GameCell) {
        val dx = otherCell.x.value - x.value
        val dy = otherCell.y.value - y.value
        val dox = x.value - otherCell.x.value
        val doy = y.value - otherCell.y.value
        x.value = x.value + dx
        y.value = y.value + dy
        otherCell.x.value = otherCell.x.value + dox
        otherCell.y.value = otherCell.y.value + doy
    }


    fun onClick() {
        rules.appendKey(face)
        rules.evaluateContent()
    }
}