package com.cricut.androidassessment

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepoModule {
    @Binds
    @Singleton
    abstract fun bindQuestionsRepository(
        questionsRepositoryImpl: QuestionsRepositoryImpl
    ): QuestionsRepository
}