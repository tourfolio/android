package com.hdb.tourfolio.core.network

import com.hdb.tourfolio.core.network.dto.TradeRequestDto
import com.hdb.tourfolio.core.network.dto.TradeResponseDto
import javax.inject.Inject

class TradeRepository
    @Inject
    constructor(
        private val tradeApiService: TradeApiService,
    ) {
        suspend fun trade(
            spotId: Long,
            type: TradeType,
            quantity: Int,
            memberId: Int = DEFAULT_MEMBER_ID,
        ): TradeResponseDto =
            tradeApiService.trade(
                TradeRequestDto(
                    memberId = memberId,
                    spotId = spotId,
                    type = type.apiValue,
                    quantity = quantity,
                ),
            )

        companion object {
            private const val DEFAULT_MEMBER_ID = 4
        }
    }
