package com.outerspace.game_of_words.ui_layer

import android.annotation.SuppressLint
import android.content.res.Configuration
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
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleOwner
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

                mainVM.liveResult.observe(this as LifecycleOwner) {
                        gameResult ->
                    stateContent = gameResult.content
                    stateDefinition = gameResult.definition
                }

                val totalWidth = remember { mutableIntStateOf(value = 320) }
                val totalHeight = remember { mutableIntStateOf(value = 250) }
                val cellList = remember { mutableStateListOf<CellSpec>() }

                val rightHanded = remember { mutableStateOf(true) }
                val topKeyboard = remember { mutableStateOf(false) }

                LaunchedEffect(key1=totalWidth, key2=totalHeight) {
                    mainVM.gameRules.cellSpecList(
                        totalWidth=totalWidth.intValue, totalHeight=totalHeight.intValue).forEach {
                        cellList.add(it)
                    }
                }

                mainVM.gameRules.liveCellList.observe(this as LifecycleOwner) {                       // forces Recomposition of changed cells.
                    if (cellList.isNotEmpty()) {
                        val lastElement = cellList.removeAt(index = cellList.size - 1)
                        cellList.add(lastElement)
                    }
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if(LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT)
                        GamePortraitScreen(totalWidth, totalHeight,
                            cellList, stateContent, stateDefinition,
                            mainVM, innerPadding, topKeyboard)
                    else
                        GameLandscapeScreen(totalWidth, totalHeight,
                            cellList, stateContent, stateDefinition,
                            mainVM, innerPadding, rightHanded)
                }
            }
        }
    }
}

private enum class Places {
    pageSpacer,
    gameKeyboard,
    definitionText,
    clearWordButton,
}

@Composable
fun GamePortraitScreen(totalWidth: MutableIntState, totalHeight: MutableIntState,
                        cellList: MutableList<CellSpec>,
                        stateContent: String, stateDefinition: String,
                        mainVM: UiLayerInterface, innerPadding: PaddingValues,
                        topKeyboard: MutableState<Boolean>) {

    val placement = if (topKeyboard.value) listOf(Places.pageSpacer, Places.gameKeyboard, Places.definitionText, Places.clearWordButton )
            else listOf(Places.pageSpacer, Places.clearWordButton, Places.definitionText, Places.gameKeyboard, Places.pageSpacer)
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues = innerPadding)
        .padding(bottom = 16.dp, start = 32.dp, end = 32.dp)) {

        for(place in placement) {
            if (place == Places.pageSpacer) {
                Spacer(Modifier
                    .fillMaxWidth()
                    .height(height = 64.dp))
            }
            if (place == Places.gameKeyboard) {
                @SuppressLint("UnusedBoxWithConstraintsScope")
                BoxWithConstraints(Modifier
                    .fillMaxWidth()
                    .weight(weight = 0.4f)) {
                    totalWidth.intValue = maxWidth.value.toInt()
                    totalHeight.intValue = maxHeight.value.toInt()

                    GameKeyboard(cellList, mainVM)
                }
            }
            if (place == Places.definitionText) {
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
                        .verticalScroll(state = rememberScrollState())
                        .weight(weight = 1f)
                )
            }
            if (place == Places.clearWordButton) {
                Button(
                    onClick = { mainVM.onClickClearButton() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(size = 8.dp)
                ) { Text(
                    text = stringResource(id = R.string.clear_word_button),
                    modifier = Modifier.padding(start = 8.dp, end = 8.dp),
                )}
            }
        }
    }
}

@Composable
fun GameLandscapeScreen(totalWidth: MutableIntState, totalHeight: MutableIntState,
                        cellList: MutableList<CellSpec>,
                        stateContent: String, stateDefinition: String,
                        mainVM: UiLayerInterface, innerPadding: PaddingValues,
                        rightHanded: MutableState<Boolean>
) {

    val places = listOf(!rightHanded.value, rightHanded.value)
    Row(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
        @SuppressLint("UnusedBoxWithConstraintsScope")
        for(place in places) {
            if (place) {
                BoxWithConstraints(Modifier.fillMaxHeight()
                    .padding(8.dp)
                    .weight(0.4f)) {
                    totalWidth.intValue = maxWidth.value.toInt()
                    totalHeight.intValue = maxHeight.value.toInt()

                    GameKeyboard(cellList, mainVM)
                }
            } else {
                Column(modifier = Modifier.weight(0.6f).padding(8.dp)) {
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
                            .verticalScroll(state = rememberScrollState())
                            .weight(weight = 1f)
                    )
                    Button(
                        onClick = { mainVM.onClickClearButton() },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(size = 8.dp)
                    ) { Text(
                        text = stringResource(id = R.string.clear_word_button),
                        modifier = Modifier.padding(start = 8.dp, end = 8.dp),
                    )}
                }
            }
        }
    }
}

@Composable
fun GameKeyboard(cellList: MutableList<CellSpec>, mainVM: UiLayerInterface) {
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
        contentPadding = PaddingValues(0.dp),
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

@Preview(showBackground = true)
@Composable
fun GamePortraitScreenPreview() {
    val fakeGameRules = GameRules(nCols = 6, nRows = 5, colSpace = 4, rowSpace = 4)
    val fakeDictionaryService = object: DictionaryApiService {
        override suspend fun getDictionaryEntry(word: String): List<DictionaryEntry> {
            return listOf()
        }
    }
    val mainVM = MainViewModel(fakeGameRules, fakeDictionaryService)
    val totalWidth = remember { mutableIntStateOf(320) }
    val totalHeight = remember { mutableIntStateOf(200) }
    val cellList = remember { mutableStateListOf<CellSpec>() }
    if (cellList.isEmpty())
        fakeGameRules.cellSpecList(totalWidth = totalWidth.intValue, totalHeight = totalHeight.intValue).forEach {
            cellList.add(it)
        }
    val stateContent = "WOLF ${cellList.size}"
    val stateDefinition = "Some kind of a dog"
    val innerPadding = PaddingValues(start=4.dp, top=24.dp, end=4.dp, bottom=4.dp)
    val placement = remember { mutableStateOf(false) }
    @SuppressLint("UnusedBoxWithConstraintsScope")
    BoxWithConstraints(Modifier.fillMaxSize()) {
        totalWidth.value = maxWidth.value.toInt()
        totalHeight.value = maxHeight.value.toInt()
        GamePortraitScreen(totalWidth, totalHeight,
            cellList,
            stateContent, stateDefinition,
            mainVM, innerPadding, placement)
    }
}


@Preview(showBackground = true, heightDp = 720, widthDp = 960)
@Composable
fun GameLandscapeScreenPreview() {
    val fakeGameRules = GameRules(nCols = 6, nRows = 5, colSpace = 4, rowSpace = 4)
    val fakeDictionaryService = object: DictionaryApiService {
        override suspend fun getDictionaryEntry(word: String): List<DictionaryEntry> {
            return listOf()
        }
    }
    val mainVM = MainViewModel(fakeGameRules, fakeDictionaryService)
    val totalWidth = remember { mutableIntStateOf(320) }
    val totalHeight = remember { mutableIntStateOf(200) }
    val cellList = remember { mutableStateListOf<CellSpec>() }
    if (cellList.isEmpty())
        fakeGameRules.cellSpecList(totalWidth = totalWidth.intValue, totalHeight = totalHeight.intValue).forEach {
            cellList.add(it)
        }
    val stateContent = "WOLF ${cellList.size}"
    val stateDefinition = "Some kind of a dog"
    val innerPadding = PaddingValues(start=4.dp, top=24.dp, end=4.dp, bottom=4.dp)
    val rightHanded = remember { mutableStateOf(false) }

    @SuppressLint("UnusedBoxWithConstraintsScope")
    BoxWithConstraints(Modifier.fillMaxSize()) {
        totalWidth.intValue = maxWidth.value.toInt()
        totalHeight.intValue = maxHeight.value.toInt()
        GameLandscapeScreen(totalWidth, totalHeight,
            cellList,
            stateContent, stateDefinition,
            mainVM, innerPadding, rightHanded)
    }
}