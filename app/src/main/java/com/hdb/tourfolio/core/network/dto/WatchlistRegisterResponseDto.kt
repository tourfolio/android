package com.hdb.tourfolio.core.network.dto

data class WatchlistRegisterResponseDto(
    val id: Long,
    val userId: Int,
    val spotId: Long,
    val createdAt: String,
)
