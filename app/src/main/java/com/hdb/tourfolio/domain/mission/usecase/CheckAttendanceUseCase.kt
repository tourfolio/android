package com.hdb.tourfolio.domain.mission.usecase

import com.hdb.tourfolio.domain.mission.model.AttendanceCheckResult
import com.hdb.tourfolio.domain.mission.repository.MissionRepository
import javax.inject.Inject

class CheckAttendanceUseCase
    @Inject
    constructor(
        private val missionRepository: MissionRepository,
    ) {
        suspend operator fun invoke(): AttendanceCheckResult = missionRepository.checkAttendance()
    }
