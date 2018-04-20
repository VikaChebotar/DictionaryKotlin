package com.mydictionary.di

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.util.LruCache
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.mydictionary.commons.OXFORD_API_ENDPOINT
import com.mydictionary.commons.getCacheMemorySize
import com.mydictionary.data.firebasestorage.InternalFirebaseStorage
import com.mydictionary.data.oxfordapi.*
import com.mydictionary.data.pojo.SearchResult
import com.mydictionary.data.pojo.WordDetails
import com.mydictionary.data.pojo.WordResponseMapper
import com.mydictionary.data.pojo.WordResponseMapperImpl
import com.mydictionary.data.repository.AllRepository
import com.mydictionary.data.repository.AllRepositoryImpl
import com.mydictionary.data.repository.WordRepositoryImpl
import com.mydictionary.domain.repository.WordRepository
import com.mydictionary.domain.usecases.SearchWordUseCase
import com.mydictionary.presentation.DictionaryApp
import com.mydictionary.presentation.viewmodel.ViewModelFactory
import com.mydictionary.presentation.viewmodel.account.AccountViewModel
import com.mydictionary.presentation.viewmodel.home.HomeViewModel
import com.mydictionary.presentation.viewmodel.learn.LearnWordsViewModel
import com.mydictionary.presentation.viewmodel.search.SearchViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
class AppModule(private val app: DictionaryApp) {

    @Provides
    @Singleton
    fun providesApplication(): DictionaryApp = app

    @Provides
    @Singleton
    fun providesFirebaseStorage(
        app: DictionaryApp,
        auth: FirebaseAuth,
        database: FirebaseDatabase
    ) = InternalFirebaseStorage(app, auth, database)

    @Provides
    @Singleton
    fun providesOxfordDictionaryStorage(
        app: DictionaryApp,
        wordsAPI: WordsAPI,
        cache: LruCache<String, WordDetails>,
        mapper: WordResponseMapper
    ) =
        OxfordDictionaryStorage(app, wordsAPI, cache, mapper)

    @Provides
    @Singleton
    fun providesRepository(
        firebaseStorage: InternalFirebaseStorage,
        oxfordDictionaryStorage: OxfordDictionaryStorage
    ): AllRepository = AllRepositoryImpl(firebaseStorage, oxfordDictionaryStorage)
}

@Module
abstract class ViewModelModule {
    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(AccountViewModel::class)
    internal abstract fun accountViewModel(viewModel: AccountViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    internal abstract fun homeViewModel(viewModel: HomeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LearnWordsViewModel::class)
    internal abstract fun learnViewModel(viewModel: LearnWordsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SearchViewModel::class)
    internal abstract fun searchViewModel(viewModel: SearchViewModel): ViewModel
}

@Module
class NetworkModule {

    @Provides
    fun providesOxfordDictionaryMapper(): WordResponseMapper = WordResponseMapperImpl

    @Provides
    @Singleton
    fun providesWordsCache() = LruCache<String, WordDetails>(getCacheMemorySize())

    @Provides
    @Singleton
    fun providesWordsApi(
        client: OkHttpClient,
        gson: Gson
    ): WordsAPI = Retrofit.Builder()
        .baseUrl(OXFORD_API_ENDPOINT)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
        .create(WordsAPI::class.java)

    @Provides
    @Singleton
    fun provideGson(): Gson {
        val searchResultListType = object : TypeToken<SearchResult>() {}.type
        return GsonBuilder().registerTypeAdapter(
            searchResultListType,
            SearchResultResponseDeserializer()
        ).create()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        connectivityInterceptor: ConnectivityInterceptor,
        headerInterceptor: HeaderInterceptor
    ): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient.Builder().addInterceptor(loggingInterceptor)
            .addInterceptor(headerInterceptor).addInterceptor(connectivityInterceptor).build();
    }

    @Provides
    @Singleton
    fun providesHeaderInterceptor(app: DictionaryApp) = HeaderInterceptor(app)

    @Provides
    @Singleton
    fun providesConnectivityInterceptor(app: DictionaryApp) = ConnectivityInterceptor(app)
}

@Module
class FirebaseModule {
    @Provides
    @Singleton
    fun providesFirebaseAuth() = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun providesFirebaseDatabase() = FirebaseDatabase.getInstance()
}

@Module
class UseCasesModule {
    @Provides
    @Singleton
    fun providesSearchWordUseCase(wordRepository: WordRepository) =
        SearchWordUseCase(wordRepository)
}

@Module
class DataModule {

    @Provides
    @Singleton
    fun providesWordRepository(
        oxfordDictionaryStorage: OxfordDictionaryStorage
    ): WordRepository = WordRepositoryImpl(oxfordDictionaryStorage)


}