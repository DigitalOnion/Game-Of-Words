package com.outerspace.game_of_words.data_layer.game

interface GameResultInterface {
    val content: String
    val definition: String
}

data class GameResult(
    override val content: String,
    override val definition: String
): GameResultInterface
