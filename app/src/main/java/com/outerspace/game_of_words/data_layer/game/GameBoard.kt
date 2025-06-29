package com.outerspace.game_of_words.data_layer.game

import com.outerspace.game_of_words.data_layer.data.DictionaryApiService
import com.outerspace.game_of_words.ui_layer.GameUIInterface
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

interface GameBoardInterface {
    fun getGameBoard(nCols: Int, nRows: Int, gameUi: GameUIInterface): List<List<GameCell>>
}

// GameBoard
class GameBoard @Inject constructor(
    val gameRules: GameRules
): GameBoardInterface {

    override fun getGameBoard(nCols: Int, nRows: Int, gameUi: GameUIInterface): List<List<GameCell>> {
        gameRules.gameUi = gameUi
        val board = List(nRows) {
            rowIndex ->
            List(nCols) {
                colIndex ->
                val face = gameRules.keyFace(rowIndex * nCols + colIndex)
                GameCell(face, gameRules)
            }
        }
        return board
    }
}