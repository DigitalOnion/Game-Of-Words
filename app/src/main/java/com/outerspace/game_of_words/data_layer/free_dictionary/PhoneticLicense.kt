package com.outerspace.game_of_words.data_layer.free_dictionary

import com.squareup.moshi.Json

class PhoneticLicense {
    @Json(name = "name")
    var name: String? = null

    @Json(name = "url")
    var url: String? = null
}
