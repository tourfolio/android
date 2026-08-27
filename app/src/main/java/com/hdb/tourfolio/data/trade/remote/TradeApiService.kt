package com.hdb.tourfolio.data.trade.remote

import com.hdb.tourfolio.data.common.network.Authenticated
import com.hdb.tourfolio.data.trade.remote.dto.TradeRequestDto
import com.hdb.tourfolio.data.trade.remote.dto.TradeResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface TradeApiService {
    @Authenticated
    @POST("api/stocks/trade")
    suspend fun trade(
        @Body request: TradeRequestDto,
    ): TradeResponseDto
}
