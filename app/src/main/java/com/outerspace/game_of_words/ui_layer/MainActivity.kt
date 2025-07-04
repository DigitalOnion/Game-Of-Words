package com.outerspace.game_of_words.ui_layer

import com.outerspace.game_of_words.R
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.outerspace.game_of_words.data_layer.game.GameCell
import com.outerspace.game_of_words.data_layer.game.GameRulesInterface
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

                val matrix = mainVM.getGameBoard(5, 6, mainVM)

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(modifier = Modifier.fillMaxSize()) {
                        Spacer(Modifier.fillMaxWidth().height(64.dp))
                        GameKeyboard(
                            modifier = Modifier.padding(innerPadding),
                            matrix
                        )
                        Text(
                            text = stateContent,
                            modifier = Modifier.padding(top = 24.dp, start = 32.dp, end = 32.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = stateDefinition,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .padding(top = 24.dp, start = 32.dp, end = 32.dp)
                                .verticalScroll(rememberScrollState())
                                .weight(1f)
                        )
                        Button(
                            onClick = { mainVM.onClickClearButton() },
                            modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp, start = 32.dp, end = 32.dp)
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
fun GameKeyboardColumnBased(modifier: Modifier = Modifier, matrix: List<List<GameCell>>) {
    Column(modifier.fillMaxWidth()) {
        matrix.forEach { row ->
            Spacer(Modifier.fillMaxWidth().height(4.dp))
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                row.forEach { cell ->
                    Button(onClick = { cell.onClick() }, modifier = Modifier, ) {
                        Text(cell.face,
                            modifier = Modifier.width(20.dp),
                            textAlign = TextAlign.Center)
                    }
                }
            }
        }
    }
}

@Composable
fun GameKeyboard(modifier: Modifier = Modifier, matrix: List<List<GameCell>>) {
    val nRows = matrix.size
    val nCols = matrix[0].size
    val margin = 32
    val colGap = 8
    val rowGap = 8
    var x = 0.dp
    var y = 0.dp
    BoxWithConstraints(modifier.fillMaxWidth().height((nRows * 45).dp)) {
        val density = LocalDensity.current
        val h = (with(density) { this@BoxWithConstraints.maxHeight.toPx() }).toInt() / density.density
        val w = (with(density) { this@BoxWithConstraints.maxWidth.toPx() }).toInt() / density.density

        val btnWidth = (w - 2 * margin - colGap * (nCols - 1)) / nCols
        val btnHeight = (h - rowGap * (nRows -1 )) / nRows

        for ((i, row) in matrix.withIndex()) {
            for ((j, cell) in row.withIndex()) {
                x = (margin + j * btnWidth + j * colGap).dp
                y = (i * btnHeight + i * rowGap).dp
                Button(onClick = { cell.onClick() },
                    modifier = Modifier.absoluteOffset(x,y).width(btnWidth.dp).height(btnHeight.dp),
                    shape = RoundedCornerShape(8.dp)) {
                    Text(cell.face,
                        modifier = Modifier.width(20.dp),
                        textAlign = TextAlign.Center)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GameKeyboardPreview() {
    val fakeGameRules: GameRulesInterface = object: GameRulesInterface {
        override fun keyFace(n: Int): String { return "x"}
        override fun appendKey(key: String) {}
        override fun content(): String { return "No-need"}
        override fun clear() {}
        override fun evaluateContent() {}
    }
    val gca = GameCell("A", fakeGameRules)
    val gcb = GameCell("B", fakeGameRules)
    val gcc = GameCell("C", fakeGameRules)
    val gcd = GameCell("D", fakeGameRules)
    val gce = GameCell("E", fakeGameRules)
    val gcf = GameCell("F", fakeGameRules)
    val gcg = GameCell("G", fakeGameRules)
    val gch = GameCell("H", fakeGameRules)
    val gci = GameCell("I", fakeGameRules)
    val gcj = GameCell("J", fakeGameRules)
    val gck = GameCell("K", fakeGameRules)
    val gcl = GameCell("L", fakeGameRules)
    val gcm = GameCell("M", fakeGameRules)
    val gcn = GameCell("N", fakeGameRules)
    val gco = GameCell("O", fakeGameRules)
    val gcp = GameCell("P", fakeGameRules)
    GameOfWordsTheme {
        val matrix = listOf(
            listOf(gca, gcb, gcc, gcd),
            listOf(gce, gcf, gcg, gch),
            listOf(gci, gcj, gck, gcl),
            listOf(gcm, gcn, gco, gcp),
        )
        GameKeyboard(Modifier, matrix)
    }
}