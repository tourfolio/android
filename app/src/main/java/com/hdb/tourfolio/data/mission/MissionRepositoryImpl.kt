package com.hdb.tourfolio.data.mission

import com.hdb.tourfolio.data.mission.mapper.toDomain
import com.hdb.tourfolio.data.mission.remote.MissionApiService
import com.hdb.tourfolio.domain.mission.model.AttendanceCalendar
import com.hdb.tourfolio.domain.mission.model.AttendanceCheckResult
import com.hdb.tourfolio.domain.mission.model.MissionOverview
import com.hdb.tourfolio.domain.mission.repository.MissionRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MissionRepositoryImpl
    @Inject
    constructor(
        private val missionApiService: MissionApiService,
    ) : MissionRepository {
        override suspend fun getMissions(): MissionOverview =
            missionApiService
                .getMissions()
                .toDomain()

        override suspend fun checkAttendance(): AttendanceCheckResult =
            missionApiService
                .checkAttendance()
                .toDomain()

        override suspend fun getAttendanceCalendar(
            year: Int,
            month: Int,
        ): AttendanceCalendar =
            missionApiService
                .getAttendanceCalendar(year = year, month = month)
                .toDomain()
    }
