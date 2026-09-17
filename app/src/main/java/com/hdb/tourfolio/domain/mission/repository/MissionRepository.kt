package com.hdb.tourfolio.domain.mission.repository

import com.hdb.tourfolio.domain.mission.model.AttendanceCalendar
import com.hdb.tourfolio.domain.mission.model.AttendanceCheckResult
import com.hdb.tourfolio.domain.mission.model.MissionClaimResult
import com.hdb.tourfolio.domain.mission.model.MissionOverview

interface MissionRepository {
    suspend fun getMissions(): MissionOverview

    suspend fun claimCollectionMission(missionId: Long): MissionClaimResult

    suspend fun checkAttendance(): AttendanceCheckResult

    suspend fun getAttendanceCalendar(
        year: Int,
        month: Int,
    ): AttendanceCalendar
}
