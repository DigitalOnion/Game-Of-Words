package com.outerspace.game_of_words.data_layer.data

import com.outerspace.game_of_words.data_layer.free_dictionary.DictionaryEntry
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

const val BASE_URL = "https://api.dictionaryapi.dev"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val moshiConverterFactory = MoshiConverterFactory.create(moshi)

val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(moshiConverterFactory)
    .build()

interface DictionaryApiService {
    @GET("/api/v2/entries/en/{word}")
    suspend fun getDictionaryEntry(@Path("word") word: String): List<DictionaryEntry>
}

//val dictionaryApiService: DictionaryApiService =
//    retrofit.create<DictionaryApiService>(DictionaryApiService::class.java)