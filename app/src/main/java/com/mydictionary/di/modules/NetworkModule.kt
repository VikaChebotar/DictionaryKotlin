package com.mydictionary.di.modules

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.mydictionary.data.wordinforepo.OXFORD_API_ENDPOINT
import com.mydictionary.data.wordinforepo.datasource.remote.ConnectivityInterceptor
import com.mydictionary.data.wordinforepo.datasource.remote.HeaderInterceptor
import com.mydictionary.data.wordinforepo.datasource.remote.RemoteWordsDataSource
import com.mydictionary.data.wordinforepo.datasource.remote.SearchResultResponseDeserializer
import com.mydictionary.data.wordinforepo.pojo.SearchResult
import com.mydictionary.presentation.DictionaryApp
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class NetworkModule {
    @Provides
    @Singleton
    fun providesWordsApi(
        client: OkHttpClient,
        gson: Gson
    ): RemoteWordsDataSource = Retrofit.Builder()
        .baseUrl(OXFORD_API_ENDPOINT)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
        .create(RemoteWordsDataSource::class.java)

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
    fun providesHeaderInterceptor(app: DictionaryApp) =
        HeaderInterceptor(app)

    @Provides
    @Singleton
    fun providesConnectivityInterceptor(app: DictionaryApp) =
        ConnectivityInterceptor(app)
}