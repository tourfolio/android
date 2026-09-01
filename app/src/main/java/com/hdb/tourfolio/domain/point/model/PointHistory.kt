package com.hdb.tourfolio.domain.point.model

data class PointHistory(
    val balance: Long,
    val entries: List<PointHistoryEntry>,
)

data class PointHistoryEntry(
    val title: String,
    val amount: Long,
    val createdAt: String,
)
