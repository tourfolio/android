package com.hdb.tourfolio.di

import com.google.gson.Gson
import com.hdb.tourfolio.core.network.AuthApiService
import com.hdb.tourfolio.core.network.AuthInterceptor
import com.hdb.tourfolio.core.network.CardApiService
import com.hdb.tourfolio.core.network.ExploreApiService
import com.hdb.tourfolio.core.network.PortfolioApiService
import com.hdb.tourfolio.core.network.StockApiService
import com.hdb.tourfolio.core.network.TradeApiService
import com.hdb.tourfolio.core.network.WatchlistApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

private const val BASE_URL = "https://tourfolio.kr/"

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        val loggingInterceptor =
            HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        gson: Gson,
    ): Retrofit =
        Retrofit.Builder()

            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

    @Provides
    @Singleton
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService = retrofit.create(AuthApiService::class.java)

    @Provides
    @Singleton
    fun provideStockApiService(retrofit: Retrofit): StockApiService = retrofit.create(StockApiService::class.java)

    @Provides
    @Singleton
    fun providePortfolioApiService(retrofit: Retrofit): PortfolioApiService = retrofit.create(PortfolioApiService::class.java)

    @Provides
    @Singleton
    fun provideCardApiService(retrofit: Retrofit): CardApiService = retrofit.create(CardApiService::class.java)

    @Provides
    @Singleton
    fun provideExploreApiService(retrofit: Retrofit): ExploreApiService = retrofit.create(ExploreApiService::class.java)

    @Provides
    @Singleton
    fun provideWatchlistApiService(retrofit: Retrofit): WatchlistApiService = retrofit.create(WatchlistApiService::class.java)

    @Provides
    @Singleton
    fun provideTradeApiService(retrofit: Retrofit): TradeApiService = retrofit.create(TradeApiService::class.java)
}
