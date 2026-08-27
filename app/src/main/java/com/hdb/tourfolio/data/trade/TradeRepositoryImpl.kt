package com.hdb.tourfolio.data.trade

import com.hdb.tourfolio.data.trade.mapper.toApiValue
import com.hdb.tourfolio.data.trade.mapper.toDomain
import com.hdb.tourfolio.data.trade.remote.TradeApiService
import com.hdb.tourfolio.data.trade.remote.dto.TradeRequestDto
import com.hdb.tourfolio.domain.auth.repository.AuthRepository
import com.hdb.tourfolio.domain.trade.model.TradeResult
import com.hdb.tourfolio.domain.trade.model.TradeType
import com.hdb.tourfolio.domain.trade.repository.TradeRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TradeRepositoryImpl
    @Inject
    constructor(
        private val tradeApiService: TradeApiService,
        private val authRepository: AuthRepository,
    ) : TradeRepository {
        override suspend fun trade(
            spotId: Long,
            type: TradeType,
            quantity: Int,
        ): TradeResult {
            val memberId = authRepository.requireLoggedInUserId().toInt()

            return tradeApiService
                .trade(
                    TradeRequestDto(
                        memberId = memberId,
                        spotId = spotId,
                        type = type.toApiValue(),
                        quantity = quantity,
                    ),
                ).toDomain()
        }
    }
