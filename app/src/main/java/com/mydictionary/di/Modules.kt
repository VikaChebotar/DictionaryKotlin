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
import com.mydictionary.data.repository.WordsRepository
import com.mydictionary.data.repository.WordsRepositoryImpl
import com.mydictionary.presentation.DictionaryApp
import com.mydictionary.presentation.viewmodel.ViewModelFactory
import com.mydictionary.presentation.viewmodel.home.HomeViewModel
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
    fun providesFirebaseStorage(app: DictionaryApp, auth: FirebaseAuth, database: FirebaseDatabase) = InternalFirebaseStorage(app, auth, database)

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
    ): WordsRepository = WordsRepositoryImpl(firebaseStorage, oxfordDictionaryStorage)
}

@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    abstract fun bindHomeViewModel(homeViewModel: HomeViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory):
            ViewModelProvider.Factory
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