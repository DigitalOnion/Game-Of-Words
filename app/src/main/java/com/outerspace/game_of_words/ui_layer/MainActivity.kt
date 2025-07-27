package com.outerspace.game_of_words.ui_layer

import android.annotation.SuppressLint
import com.outerspace.game_of_words.R
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.outerspace.game_of_words.data_layer.data.DictionaryApiService
import com.outerspace.game_of_words.data_layer.free_dictionary.DictionaryEntry
import com.outerspace.game_of_words.data_layer.game.CellSpec
import com.outerspace.game_of_words.data_layer.game.GameRules
import com.outerspace.game_of_words.ui.theme.GameOfWordsTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val mainVM: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            GameOfWordsTheme {
                var stateContent by remember { mutableStateOf("...") }
                var stateDefinition by remember { mutableStateOf("") }

                mainVM.liveResult.observe(this) {
                        gameResult ->
                    stateContent = gameResult.content
                    stateDefinition = gameResult.definition
                }

                var totalWidth = remember { 320 }
                var totalHeight = remember { 250 }
                var cellList = remember { mutableStateListOf<CellSpec>() }

                LaunchedEffect(totalWidth, totalHeight) {
                    mainVM.gameRules.cellSpecList(totalWidth, totalHeight).forEach {
                        cellList.add(it)
                    }
                }

                mainVM.gameRules.liveCellList.observe(this) {
                    val lastElement = cellList.removeAt(cellList.size - 1)
                    cellList.add(lastElement)
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(bottom = 16.dp, start = 32.dp, end = 32.dp)) {
                        Spacer(Modifier
                            .fillMaxWidth()
                            .height(64.dp))

                        @SuppressLint("UnusedBoxWithConstraintsScope")
                        BoxWithConstraints(Modifier
                            .fillMaxWidth()
                            .weight(weight = 0.4f)) {
                            totalWidth = maxWidth.value.toInt()
                            totalHeight = maxHeight.value.toInt()

                            GameKeyboard(
                                cellList,
                                mainVM,
                            )
                        }

                        Text(
                            text = stateContent,
                            modifier = Modifier.padding(top = 24.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = stateDefinition,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .padding(top = 24.dp)
                                .verticalScroll(rememberScrollState())
                                .weight(1f)
                        )
                        Button(
                            onClick = { mainVM.onClickClearButton() },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) { Text(
                            text = stringResource(R.string.clear_word_button),
                            modifier = Modifier.padding(start = 8.dp, end = 8.dp),
                        )}
                    }
                }
            }
        }
    }
}

@Composable
fun GameKeyboard(cellList: MutableList<CellSpec>, mainVM: MainViewModel) {
        cellList.forEach {
            SpecialButton(x = it.x, y = it.y,
                cell = it, cellList = cellList,
                onClick = { mainVM.onClickKeyboardButton(cell = it, cellList = cellList) }
            )
        }
    }

@Composable
fun SpecialButton(x: Float, y: Float, cell: CellSpec, cellList: MutableList<CellSpec>, onClick: () -> Unit) {
    val offset by animateOffsetAsState(
        targetValue = Offset(x = x, y = y),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioHighBouncy,
            stiffness = Spring.StiffnessMedium)
    )
    Button(onClick = onClick, modifier = Modifier
        .height(height = cell.height.dp)
        .width(width = cell.width.dp)
        .offset {
            IntOffset(
                (offset.x.dp.value * density).toInt(),
                (offset.y.dp.value * density).toInt()
            )
        },
        shape = RoundedCornerShape(size = 8.dp)) {
        Text(text = cell.face, modifier = Modifier)
        }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Preview(showBackground = true)
@Composable
fun GameKeyboardPreview() {
    val fakeGameRules = GameRules(nCols = 6, nRows = 5, colSpace = 4, rowSpace = 4)
    val fakeDictionaryService = object: DictionaryApiService {
        override suspend fun getDictionaryEntry(word: String): List<DictionaryEntry> {
            return listOf()
        }
    }
    val mainVM = MainViewModel(fakeGameRules, fakeDictionaryService)
    GameOfWordsTheme {
        BoxWithConstraints(Modifier
            .fillMaxWidth()
            .height(300.dp)) {
            val w = maxWidth.value.toInt()
            val h = maxHeight.value.toInt()
            val cellList = remember { mutableStateListOf<CellSpec>() }
            fakeGameRules.cellSpecList(w, h).forEach {
                cellList.add(it)
            }

            GameKeyboard(
                cellList,
                mainVM)
        }
    }
}