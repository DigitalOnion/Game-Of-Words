package com.outerspace.game_of_words.data_layer.free_dictionary

import com.squareup.moshi.Json

class Meaning {
    @Json(name = "partOfSpeech")
    var partOfSpeech: String? = null

    @Json(name = "definitions")
    var definitions: MutableList<Definition?>? = null

    @Json(name = "synonyms")
    var synonyms: MutableList<String?>? = null

    @Json(name = "antonyms")
    var antonyms: MutableList<String?>? = null
}
