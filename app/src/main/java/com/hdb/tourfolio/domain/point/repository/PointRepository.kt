package com.hdb.tourfolio.domain.point.repository

import com.hdb.tourfolio.domain.point.model.PointHistory

interface PointRepository {
    suspend fun getPointHistory(): PointHistory
}
