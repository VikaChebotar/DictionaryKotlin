package com.mydictionary.di

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.mydictionary.data.userrepo.UserMapper
import com.mydictionary.data.userrepo.UserRepositoryImpl
import com.mydictionary.data.userwordrepo.UserWordMapper
import com.mydictionary.data.userwordrepo.UserWordsRepositoryImpl
import com.mydictionary.data.wordinforepo.*
import com.mydictionary.data.wordinforepo.pojo.SearchResult
import com.mydictionary.data.wordinforepo.restapi.ConnectivityInterceptor
import com.mydictionary.data.wordinforepo.restapi.HeaderInterceptor
import com.mydictionary.data.wordinforepo.restapi.SearchResultResponseDeserializer
import com.mydictionary.data.wordinforepo.restapi.WordsAPI
import com.mydictionary.data.wordlistrepo.WordListMapper
import com.mydictionary.data.wordlistrepo.WordListRepositoryImpl
import com.mydictionary.domain.repository.UserRepository
import com.mydictionary.domain.repository.UserWordRepository
import com.mydictionary.domain.repository.WordListRepository
import com.mydictionary.domain.repository.WordRepository
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
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton


@Module
class AppModule(private val app: DictionaryApp) {

    @Provides
    @Singleton
    fun providesApplication(): DictionaryApp = app

    @Provides
    @Named("executor_thread")
    fun provideExecutorThread(): Scheduler {
        return Schedulers.io()
    }

    @Provides
    @Named("ui_thread")
    fun provideUiThread(): Scheduler {
        return AndroidSchedulers.mainThread()
    }
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
class DataModule {

    @Provides
    @Singleton
    fun providesWordRepository(
            context: DictionaryApp,
            wordsApi: WordsAPI,
            wordsCache: WordInfoCache,
            mapper: WordInfoMapper
    ): WordRepository = WordRepositoryImpl(context, wordsApi, wordsCache, mapper)

    @Provides
    @Singleton
    fun providesUserWordsRepository(
            firebaseDb: FirebaseDatabase,
            firebaseAuth: FirebaseAuth,
            userWordMapper: UserWordMapper,
            context: DictionaryApp
    ): UserWordRepository = UserWordsRepositoryImpl(firebaseDb, firebaseAuth, context, userWordMapper)

    @Provides
    @Singleton
    fun providesUserRepository(context: DictionaryApp,
                               firebaseAuth: FirebaseAuth,
                               userMapper: UserMapper): UserRepository =
            UserRepositoryImpl(context, firebaseAuth, userMapper)

    @Provides
    @Singleton
    fun providesWordListRepository(firebaseDatabase: FirebaseDatabase,
                                   wordListMapper: WordListMapper): WordListRepository =
            WordListRepositoryImpl(firebaseDatabase, wordListMapper)


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

}