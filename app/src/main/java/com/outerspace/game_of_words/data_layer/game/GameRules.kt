package com.outerspace.game_of_words.data_layer.game

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.outerspace.game_of_words.data_layer.data.DictionaryApiService
import com.outerspace.game_of_words.ui_layer.UiLayerInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.random.Random


private const val allChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"

class GameRules(private val nCols: Int, private val nRows: Int,
                private val colSpace: Int, private val rowSpace: Int,
) {

    val liveCellList: MutableLiveData<MutableList<CellSpec>> by lazy {
        MutableLiveData<MutableList<CellSpec>>()
    }

    fun buttonWidth(totalWidth: Int) = ((totalWidth - (nCols - 1) * colSpace) / nCols).toFloat()
    fun buttonHeight(totalHeight: Int) = ((totalHeight - (nRows - 1) * rowSpace) / nRows).toFloat()

    fun cellSpecList(totalWidth: Int, totalHeight: Int): List<CellSpec> {
        val scrambledChars = allChars.toCharArray()
        val btnWidth = buttonWidth(totalWidth)
        val btnHeight = buttonHeight(totalHeight)

        return List(nCols * nRows) { idx ->
            val iCol = idx % nCols
            val iRow = (idx - iCol) / nRows
            val x = iCol * (btnWidth + colSpace)
            val y = iRow * (btnHeight + rowSpace)
            CellSpec(scrambledChars[idx % scrambledChars.size].toString(), x, y, btnWidth, btnHeight)
        }
    }

    private val buffer = StringBuilder()
    fun append(key: String) {
        buffer.append(key)
    }

    fun content(): String {
        return buffer.toString()
    }

    fun clear() {
        buffer.clear()
    }

    fun evaluateContent(content: String, scope: CoroutineScope, uiLayer: UiLayerInterface, dictionaryService: DictionaryApiService
    ) {
        Log.d("GAME CELL", content)
        scope.launch {
            try {
                val entryList = dictionaryService.getDictionaryEntry(content)
                val entryBuffer = StringBuffer()
                entryList.forEach {
                    it.meanings?.forEach {
                        it?.definitions?.forEach {
                            entryBuffer.append(" - ").append(it?.definition).append("\r\n")
                        }
                    }
                }
                val entry = entryBuffer.toString()
                Log.d("ENTRY", "Entry : $entry")
                uiLayer.liveResult.value = object: GameResultInterface {
                    override val content: String = content
                    override val definition: String = entry
                }
            } catch (e: Exception) {
                val nonExisting = "Non existing word."
                if (content.isEmpty()) {
                    Log.d("Entry", "empty content")
                    uiLayer.liveResult.value = object: GameResultInterface {
                        override val content: String = "..."
                        override val definition: String = ""
                    }
                } else {
                    Log.d("ENTRY", "$content: $nonExisting")
                    uiLayer.liveResult.value = object: GameResultInterface {
                        override val content: String = content
                        override val definition: String = nonExisting
                    }
                }
            }
        }
    }

//    val onClick: (cell: CellSpec, cellList: MutableList<CellSpec>) -> Unit = {cell, cellList ->
//        Log.d("ON-CLICK:", "Clicking on: face: ${cell.face}, (x, y): (${cell.x},${cell.y})")
//        swapCells(cell, cellList)
//
//    }

    fun swapCells(it: CellSpec, cellList: MutableList<CellSpec>) {
        val idx1 = cellList.indexOf(it)
        var idx2: Int
        do {
            idx2 = Random.nextInt(cellList.size)
        } while (idx2 == idx1)

        val element2 = cellList[idx2]
        val xTemp = it.x
        val yTemp = it.y
        it.x = element2.x
        it.y = element2.y
        element2.x = xTemp
        element2.y = yTemp

        liveCellList.value = cellList

        val logBld = cellList.fold(StringBuilder()) { sb, elem -> sb.append(elem.face).append(":").append(elem.x).append(",").append(elem.y).append(" - ")}
        Log.d("ELEMENT LIST", "size: ${cellList.size} : ${logBld.toString()}")
    }
}

