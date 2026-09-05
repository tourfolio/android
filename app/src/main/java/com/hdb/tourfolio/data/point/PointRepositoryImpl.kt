package com.hdb.tourfolio.data.point

import com.hdb.tourfolio.data.point.mapper.toDomain
import com.hdb.tourfolio.data.point.remote.PointApiService
import com.hdb.tourfolio.domain.point.model.PointHistory
import com.hdb.tourfolio.domain.point.repository.PointRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PointRepositoryImpl
    @Inject
    constructor(
        private val pointApiService: PointApiService,
    ) : PointRepository {
        override suspend fun getPointHistory(): PointHistory =
            pointApiService
                .getPointHistory()
                .toDomain()
    }
