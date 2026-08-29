package com.hdb.tourfolio.data.home

import com.hdb.tourfolio.data.home.mapper.toDomain
import com.hdb.tourfolio.data.home.remote.HomeApiService
import com.hdb.tourfolio.domain.home.model.Home
import com.hdb.tourfolio.domain.home.repository.HomeRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeRepositoryImpl
@Inject
constructor(
    private val homeApiService: HomeApiService,
) : HomeRepository {

    override suspend fun getHome(): Home =
        homeApiService
            .getHome()
            .toDomain()
}