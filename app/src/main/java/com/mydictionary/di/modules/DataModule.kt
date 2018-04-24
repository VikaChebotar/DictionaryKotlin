package com.mydictionary.di.modules

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.mydictionary.data.userrepo.UserRepositoryImpl
import com.mydictionary.data.userrepo.datasource.RemoteUserDataSourceImpl
import com.mydictionary.data.userrepo.datasource.UserDataSource
import com.mydictionary.data.userrepo.datasource.UserMapper
import com.mydictionary.data.userwordrepo.UserWordMapper
import com.mydictionary.data.userwordrepo.UserWordsRepositoryImpl
import com.mydictionary.data.userwordrepo.datasource.RemoteUserWordsDataSourceImpl
import com.mydictionary.data.userwordrepo.datasource.UserWordsDataSource
import com.mydictionary.data.wordinforepo.WordRepositoryImpl
import com.mydictionary.data.wordinforepo.datasource.cache.WordInfoCache
import com.mydictionary.data.wordinforepo.datasource.cache.WordInfoCacheImpl
import com.mydictionary.data.wordinforepo.datasource.remote.RemoteWordsDataSource
import com.mydictionary.data.wordinforepo.datasource.remote.WordInfoMapper
import com.mydictionary.data.wordlistrepo.WordListMapper
import com.mydictionary.data.wordlistrepo.WordListRepositoryImpl
import com.mydictionary.data.wordlistrepo.datasource.cache.WordListCache
import com.mydictionary.data.wordlistrepo.datasource.cache.WordListCacheImpl
import com.mydictionary.data.wordlistrepo.datasource.remote.RemoteWordListDataSourceImpl
import com.mydictionary.data.wordlistrepo.datasource.remote.WordListDataSource
import com.mydictionary.domain.repository.UserRepository
import com.mydictionary.domain.repository.UserWordRepository
import com.mydictionary.domain.repository.WordListRepository
import com.mydictionary.domain.repository.WordRepository
import com.mydictionary.presentation.DictionaryApp
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DataModule {

    @Provides
    @Singleton
    fun providesWordRepository(
        context: DictionaryApp,
        wordsApi: RemoteWordsDataSource,
        wordsCache: WordInfoCache,
        mapper: WordInfoMapper
    ): WordRepository = WordRepositoryImpl(context, wordsApi, wordsCache, mapper)

    @Provides
    @Singleton
    fun providesUserWordsRepository(
        userWordsDataSource: UserWordsDataSource,
        userWordMapper: UserWordMapper
    ): UserWordRepository =
        UserWordsRepositoryImpl(userWordsDataSource, userWordMapper)

    @Provides
    @Singleton
    fun providesUserRepository(
        userDataSource: UserDataSource
    ): UserRepository =
        UserRepositoryImpl(userDataSource)

    @Provides
    @Singleton
    fun providesWordListRepository(
        wordListCache: WordListCache,
        wordListDataSource: WordListDataSource,
        wordListMapper: WordListMapper
    ): WordListRepository =
        WordListRepositoryImpl(wordListDataSource, wordListCache, wordListMapper)


    @Provides
    fun providesUserMapper() = UserMapper

    @Provides
    fun providesUserWordMapper() = UserWordMapper

    @Provides
    fun providesWordListMapper() = WordListMapper

    @Provides
    fun providesWordInfoMapper() = WordInfoMapper

    @Provides
    @Singleton
    fun providesWordsCache(): WordInfoCache = WordInfoCacheImpl()

    @Provides
    @Singleton
    fun providesUserDataSource(
        context: DictionaryApp,
        firebaseAuth: FirebaseAuth,
        userMapper: UserMapper
    ): UserDataSource = RemoteUserDataSourceImpl(context, firebaseAuth, userMapper)

    @Provides
    @Singleton
    fun providesWordListDataSource(firebaseDatabase: FirebaseDatabase): WordListDataSource =
        RemoteWordListDataSourceImpl(firebaseDatabase)

    @Provides
    @Singleton
    fun providesWordListCache(): WordListCache = WordListCacheImpl()

    @Provides
    @Singleton
    fun providesUserWordsDataSource(
        firebaseDb: FirebaseDatabase,
        firebaseAuth: FirebaseAuth,
        context: DictionaryApp
    ): UserWordsDataSource = RemoteUserWordsDataSourceImpl(firebaseDb, firebaseAuth, context)
}