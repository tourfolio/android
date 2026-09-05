package com.hdb.tourfolio.data.point.remote.dto

data class PointHistoryResponseDto(
    val balance: Long,
    val histories: List<PointHistoryItemDto>,
)

data class PointHistoryItemDto(
    val title: String,
    val amount: Long,
    val createdAt: String,
)
