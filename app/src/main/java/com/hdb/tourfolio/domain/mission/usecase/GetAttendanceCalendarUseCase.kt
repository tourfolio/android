package com.hdb.tourfolio.domain.mission.usecase

import com.hdb.tourfolio.domain.mission.model.AttendanceCalendar
import com.hdb.tourfolio.domain.mission.repository.MissionRepository
import javax.inject.Inject

class GetAttendanceCalendarUseCase
    @Inject
    constructor(
        private val missionRepository: MissionRepository,
    ) {
        suspend operator fun invoke(
            year: Int,
            month: Int,
        ): AttendanceCalendar = missionRepository.getAttendanceCalendar(year = year, month = month)
    }
