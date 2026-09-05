package com.hdb.tourfolio.data.point.mapper

import com.hdb.tourfolio.data.point.remote.dto.PointHistoryItemDto
import com.hdb.tourfolio.data.point.remote.dto.PointHistoryResponseDto
import com.hdb.tourfolio.domain.point.model.PointHistory
import com.hdb.tourfolio.domain.point.model.PointHistoryEntry

fun PointHistoryResponseDto.toDomain(): PointHistory =
    PointHistory(
        balance = balance,
        entries =
            histories.map { history ->
                history.toDomain()
            },
    )

private fun PointHistoryItemDto.toDomain(): PointHistoryEntry =
    PointHistoryEntry(
        title = title,
        amount = amount,
        createdAt = createdAt,
    )
