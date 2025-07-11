package com.outerspace.game_of_words.data_layer.game

import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.shape.RoundedCornerShape
import kotlin.random.Random

object GameBoard {
    private lateinit var matrix: List<List<GameCell>>

    lateinit var gameRules: GameRules
    var nCols = 0
    var nRows = 0

    fun initializeBoard(nCols: Int, nRows: Int, gameRules: GameRules) {
        this.nCols = nCols
        this.nRows = nRows
        this.gameRules = gameRules

    }

    data class ButtonSpaceParams(
        val h: Float,
        val w: Float,
        val margin: Int,
        val colGap: Int,
        val rowGap: Int)

    @Composable
    fun buttonList(bsp: ButtonSpaceParams): MutableList<@Composable() () -> Unit> {
        val allButtonList: MutableList<@Composable() () -> Unit> = mutableListOf()

        val btnWidth = ((bsp.w - 2 * bsp.margin - bsp.colGap * (GameBoard.nCols - 1)) / GameBoard.nCols).toInt()
        val btnHeight = ((bsp.h - bsp.rowGap * (GameBoard.nRows -1 )) / GameBoard.nRows).toInt()

        matrix = List(nRows) {
        rowIndex ->
            List(nCols) {
                colIndex ->
                    val face = gameRules.keyFace(rowIndex * nCols + colIndex)
                    val xBtn = bsp.margin + colIndex * btnWidth + colIndex * bsp.colGap
                    val yBtn = rowIndex * btnHeight + rowIndex * bsp.rowGap
                    val cell = GameCell(face, gameRules)
                    cell.rememberCoordinates(xBtn, yBtn)
            }
        }
        for ((i, row) in matrix.withIndex()) {
            for ((j, cell) in row.withIndex()) {
                allButtonList.add {
                    Button(
                        onClick = {
                            cell.onClick()
                            val otherCell = matrix[Random.nextInt(nRows)][Random.nextInt(nCols)]
                            cell.swapCellPosition(otherCell)
                        },
                        modifier = Modifier.absoluteOffset(cell.x.value, cell.y.value).width(btnWidth.dp)
                            .height(btnHeight.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            cell.face,
                            modifier = Modifier.width(20.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
        return allButtonList
    }
}
