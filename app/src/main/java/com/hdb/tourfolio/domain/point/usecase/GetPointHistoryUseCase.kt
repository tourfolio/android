package com.hdb.tourfolio.domain.point.usecase

import com.hdb.tourfolio.domain.point.model.PointHistory
import com.hdb.tourfolio.domain.point.repository.PointRepository
import javax.inject.Inject

class GetPointHistoryUseCase
    @Inject
    constructor(
        private val pointRepository: PointRepository,
    ) {
        suspend operator fun invoke(): PointHistory = pointRepository.getPointHistory()
    }
