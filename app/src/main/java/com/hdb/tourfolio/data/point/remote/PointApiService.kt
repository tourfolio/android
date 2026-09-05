package com.hdb.tourfolio.data.point.remote

import com.hdb.tourfolio.data.common.network.Authenticated
import com.hdb.tourfolio.data.point.remote.dto.PointHistoryResponseDto
import retrofit2.http.GET

interface PointApiService {
    @Authenticated
    @GET("api/v1/points/history")
    suspend fun getPointHistory(): PointHistoryResponseDto
}
