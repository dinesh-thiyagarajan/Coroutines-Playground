package com.dineshworkspace.coroutinesplayground.di

import com.dineshworkspace.coroutinesplayground.HeavyProcessor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    fun providesHeavyProcessor() = HeavyProcessor()
}