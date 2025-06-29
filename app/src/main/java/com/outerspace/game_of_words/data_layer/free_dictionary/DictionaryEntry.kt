package com.outerspace.game_of_words.data_layer.free_dictionary

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class DictionaryEntry {
    @com.squareup.moshi.Json(name = "word")
    var word: kotlin.String? = null

    @com.squareup.moshi.Json(name = "phonetics")
    var phonetics: kotlin.collections.MutableList<Phonetic?>? = null

    @com.squareup.moshi.Json(name = "meanings")
    var meanings: kotlin.collections.MutableList<Meaning?>? = null

    @com.squareup.moshi.Json(name = "license")
    var license: DictionaryLicense? = null

    @com.squareup.moshi.Json(name = "sourceUrls")
    var sourceUrls: kotlin.collections.MutableList<kotlin.String?>? = null
}
