package com.hdb.tourfolio.core.network

import com.hdb.tourfolio.core.network.dto.TradeRequestDto
import com.hdb.tourfolio.core.network.dto.TradeResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface TradeApiService {
    @Authenticated
    @POST("api/stocks/trade")
    suspend fun trade(
        @Body request: TradeRequestDto,
    ): TradeResponseDto
}
