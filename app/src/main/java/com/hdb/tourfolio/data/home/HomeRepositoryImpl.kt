package com.hdb.tourfolio.data.home

import com.hdb.tourfolio.data.home.mapper.toDomain
import com.hdb.tourfolio.data.home.remote.HomeApiService
import com.hdb.tourfolio.domain.card.repository.CardRepository
import com.hdb.tourfolio.domain.home.model.Home
import com.hdb.tourfolio.domain.home.model.HomeCardCollection
import com.hdb.tourfolio.domain.home.repository.HomeRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeRepositoryImpl
    @Inject
    constructor(
        private val homeApiService: HomeApiService,
        private val cardRepository: CardRepository,
    ) : HomeRepository {
        override suspend fun getHome(): Home {
            val response = homeApiService.getHome()
            val collection = cardRepository.getCollection()
            return response.toDomain(
                cardCollection =
                    HomeCardCollection(
                        ownedCount = collection.ownedCount,
                        totalCount = collection.totalCount,
                        collectionRate = collection.collectionRate,
                    ),
            )
        }
    }
