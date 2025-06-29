package com.outerspace.game_of_words.data_layer.free_dictionary

import com.squareup.moshi.Json

class Phonetic {
    @Json(name = "audio")
    var audio: String? = null

    @Json(name = "sourceUrl")
    var sourceUrl: String? = null

    @Json(name = "license")
    var license: PhoneticLicense? = null

    @Json(name = "text")
    var text: String? = null
}
