package com.outerspace.game_of_words.dependency_injection

import com.outerspace.game_of_words.data_layer.data.DictionaryApiService
import com.outerspace.game_of_words.data_layer.data.retrofit
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object GameModule {
    @Provides
    fun provideDictionaryService(): DictionaryApiService {
        return retrofit.create<DictionaryApiService>(DictionaryApiService::class.java)
    }
}