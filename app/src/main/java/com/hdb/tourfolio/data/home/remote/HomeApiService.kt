package com.hdb.tourfolio.data.home.remote

import com.hdb.tourfolio.data.common.network.Authenticated
import com.hdb.tourfolio.data.home.remote.dto.HomeResponseDto
import retrofit2.http.GET

interface HomeApiService {
    @Authenticated
    @GET("api/v1/home")
    suspend fun getHome(): HomeResponseDto
}
