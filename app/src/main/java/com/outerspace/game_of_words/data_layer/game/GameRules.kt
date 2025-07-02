package com.outerspace.game_of_words.data_layer.game

import android.util.Log
import com.outerspace.game_of_words.data_layer.data.DictionaryApiService
import com.outerspace.game_of_words.ui_layer.GameUIInterface
import kotlinx.coroutines.launch
import javax.inject.Inject

const val allChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"

interface GameRulesInterface {
    fun keyFace(n: Int): String
    fun appendKey(key: String)
    fun content(): String
    fun clear()
    fun evaluateContent()
}

class GameRules @Inject constructor(
    val dictionaryService: DictionaryApiService
): GameRulesInterface {
    lateinit var gameUi: GameUIInterface

    val faces = allChars.toList().shuffled()
    override fun keyFace(n: Int): String {
        return faces[n % allChars.length ].toString()
    }

    private val buffer = StringBuilder()
    override fun appendKey(key: String) {
        buffer.append(key)
    }

    override fun content(): String {
        return buffer.toString()
    }

    override fun clear() {
        buffer.clear()
    }

    override fun evaluateContent() {
        val content = buffer.toString()
        Log.d("GAME CELL", content)
        gameUi.scope.launch {
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
                gameUi.evaluateResult(object: GameResultInterface {
                    override val content: String = content
                    override val definition: String = entry
                })
            } catch (e: Exception) {
                val nonExisting = "Non existing word."
                if (content.isEmpty()) {
                    Log.d("Entry", "empty content")
                    gameUi.evaluateResult(object: GameResultInterface {
                        override val content: String = "..."
                        override val definition: String = ""
                    })
                } else {
                    Log.d("ENTRY", "$content: $nonExisting")
                    gameUi.evaluateResult(object: GameResultInterface {
                        override val content: String = content
                        override val definition: String = nonExisting
                    })
                }
            }
        }
    }
}


