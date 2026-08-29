package com.hdb.tourfolio.di

import com.hdb.tourfolio.data.auth.AuthRepositoryImpl
import com.hdb.tourfolio.data.card.CardRepositoryImpl
import com.hdb.tourfolio.data.explore.ExploreRepositoryImpl
import com.hdb.tourfolio.data.home.HomeRepositoryImpl
import com.hdb.tourfolio.data.mypage.MyPageRepositoryImpl
import com.hdb.tourfolio.data.notification.NotificationRepositoryImpl
import com.hdb.tourfolio.data.portfolio.PortfolioRepositoryImpl
import com.hdb.tourfolio.data.stock.StockRepositoryImpl
import com.hdb.tourfolio.data.trade.TradeRepositoryImpl
import com.hdb.tourfolio.data.watchlist.WatchlistRepositoryImpl
import com.hdb.tourfolio.domain.auth.repository.AuthRepository
import com.hdb.tourfolio.domain.card.repository.CardRepository
import com.hdb.tourfolio.domain.explore.repository.ExploreRepository
import com.hdb.tourfolio.domain.home.repository.HomeRepository
import com.hdb.tourfolio.domain.mypage.repository.MyPageRepository
import com.hdb.tourfolio.domain.notification.repository.NotificationRepository
import com.hdb.tourfolio.domain.portfolio.repository.PortfolioRepository
import com.hdb.tourfolio.domain.stock.repository.StockRepository
import com.hdb.tourfolio.domain.trade.repository.TradeRepository
import com.hdb.tourfolio.domain.watchlist.repository.WatchlistRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindStockRepository(impl: StockRepositoryImpl): StockRepository

    @Binds
    @Singleton
    abstract fun bindPortfolioRepository(impl: PortfolioRepositoryImpl): PortfolioRepository

    @Binds
    @Singleton
    abstract fun bindTradeRepository(impl: TradeRepositoryImpl): TradeRepository

    @Binds
    @Singleton
    abstract fun bindWatchlistRepository(impl: WatchlistRepositoryImpl): WatchlistRepository

    @Binds
    @Singleton
    abstract fun bindCardRepository(impl: CardRepositoryImpl): CardRepository

    @Binds
    @Singleton
    abstract fun bindExploreRepository(impl: ExploreRepositoryImpl): ExploreRepository

    @Binds
    @Singleton
    abstract fun bindMyPageRepository(impl: MyPageRepositoryImpl): MyPageRepository

    @Binds
    @Singleton
    abstract fun bindNotificationRepository(impl: NotificationRepositoryImpl): NotificationRepository

    @Binds
    @Singleton
    abstract fun bindHomeRepository(impl: HomeRepositoryImpl): HomeRepository
}
